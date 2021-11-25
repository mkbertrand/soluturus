package soluturus.utils.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Integer;
import soluturus.base.expressions.Variable;
import soluturus.calculations.Fraction;

/**
 * The Simplifier converts strings and other formats into operable expressions.
 * The source code for this project was lifted directly from the previous Lambda
 * project with slight modifications made.
 * 
 * @author Miles K. Bertrand
 * 
 */
public final class Simplifier {

	static final TreeMap<Character, Expression> COMMON_VALUES = new TreeMap<>();
	static final TreeMap<String, Shortcut> SHORTCUTS = new TreeMap<>();
	static final Predicate<Character> NUMBER_CHARACTER = c -> Character.getType(c) == Character.DECIMAL_DIGIT_NUMBER
			|| c == '-' || c == '.';

	private static final Object lock = new Object();

	static {

		COMMON_VALUES.put('π', Expression.pi);

		COMMON_VALUES.put('e', Expression.e);
		COMMON_VALUES.put('i', Expression.i);

		SHORTCUTS.put("log", new Shortcut(n -> n.length == 1 ? n[0].log(Expression.ten) : n[0].log(n[1]), 1, 2));
		SHORTCUTS.put("ln", new Shortcut(Expression::ln));

	}

	private static final Expression parse(BigDecimal b) {
		if (b.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0)
			return new Integer(b.toBigIntegerExact());
		else {
			BigDecimal pow = BigDecimal.TEN.pow(b.toString().length() - 1 - b.toString().indexOf('.'));
			return new Integer(b.multiply(pow).toBigIntegerExact()).divide(new Integer(pow.toBigIntegerExact()));
		}
	}

	public static final Expression simplify(final Object... expression) {

		for (int i = 0; i < expression.length; i++) {

			if (expression[i] instanceof Object[])
				expression[i] = simplify((Object[]) expression[i]);
			else if (expression[i]instanceof Double doubi)
				expression[i] = parse(new BigDecimal(doubi));
			else if (expression[i] instanceof java.lang.Number)
				expression[i] = new Fraction(((java.lang.Number) expression[i]).longValue());
			else if (expression[i] instanceof BigInteger)
				expression[i] = new Fraction((BigInteger) expression[i]);
			else if (expression[i]instanceof BigDecimal bd)
				expression[i] = parse(bd);

			else if (expression[i] instanceof Character)
				switch (((Character) expression[i])) {
				case '+':
					expression[i] = Operator.ADD;
					break;
				case '-':
					expression[i] = Operator.SUBTRACT;
					break;
				case '*':
					expression[i] = Operator.MULTIPLY;
					break;
				case '/':
					expression[i] = Operator.DIVIDE;
					break;
				case '^':
					expression[i] = Operator.POW;
					break;
				default:
					if (!Variable.isAcceptableName((Character) expression[i]))
						throw new IllegalArgumentException(expression[i].toString());
					else if (Simplifier.COMMON_VALUES.containsKey(expression[i]))
						expression[i] = Simplifier.COMMON_VALUES.get(expression[i]);
					else
						expression[i] = new Variable(((Character) expression[i]).toString());

				}

			else if (!(expression[i] instanceof Symbol))
				throw new IllegalArgumentException();
		}

		for (Object o : expression)
			assert o instanceof Symbol;

		return simplify((Symbol[]) expression);
	}

	public static final Expression simplify(final Symbol... expression) {

		ArrayList<Symbol> exprlist = new ArrayList<>(Arrays.asList(expression));

		int index;
		while ((index = exprlist.indexOf(Operator.POW)) != -1) {
			exprlist.set(index - 1, ((Expression) exprlist.get(index - 1)).pow((Expression) exprlist.get(index + 1)));
			exprlist.remove(index + 1);
			exprlist.remove(index);
		}

		// The first and last elements in an expression must be operands,
		// that is to say numbers, so they needn't be checked for whether they are a
		// type of operand.
		for (int i = 1; i < exprlist.size() - 1; i++)
			if (exprlist.get(i) == Operator.MULTIPLY) {
				exprlist.set(i - 1, ((Expression) exprlist.get(i - 1)).multiply((Expression) exprlist.get(i + 1)));
				exprlist.remove(i + 1);
				exprlist.remove(i--);
			} else if (exprlist.get(i) == Operator.DIVIDE) {
				exprlist.set(i - 1, ((Expression) exprlist.get(i - 1)).divide((Expression) exprlist.get(i + 1)));
				exprlist.remove(i + 1);
				exprlist.remove(i--);
			}

		for (int i = 1; i < exprlist.size() - 1; i++)
			if (exprlist.get(i) == Operator.ADD) {
				exprlist.set(i - 1, ((Expression) exprlist.get(i - 1)).add((Expression) exprlist.get(i + 1)));
				exprlist.remove(i + 1);
				exprlist.remove(i--);
			} else if (exprlist.get(i) == Operator.SUBTRACT) {
				exprlist.set(i - 1, ((Expression) exprlist.get(i - 1)).subtract((Expression) exprlist.get(i + 1)));
				exprlist.remove(i + 1);
				exprlist.remove(i--);
			}

		// If all goes well, the result should have a size() of one. If not, the user
		// passed a bad expression.
		if (exprlist.size() != 1) {
			System.out.println(Arrays.toString(expression));
			System.out.println(exprlist);

			throw new IllegalArgumentException();
		}

		return (Expression) exprlist.get(0);
	}

	public static final Expression simplify(final String expression) {
		return simplify(parse(expression));
	}

	/**
	 * The parse method converts the input String to an array of Symbols (commonly
	 * referred to in documentation as an 'expression').
	 * <p>
	 * Expressions may contain any arithmetic operation, as well as logarithms and
	 * other predefined and user defined operations and parenthetical grouping.
	 * <p>
	 * The parser converts all whitespace to the standard space and then either
	 * removes all space, or removes redundant spacing, depending on the notation.
	 * <p>
	 * The parser converts numbers into lambda numbers, and converts variables
	 * containing only valid characters into lambda variables.
	 * <p>
	 * This method is completely safe from attack.
	 * 
	 * @param notation the Notation to use
	 * @param input    the input String to parse
	 * 
	 * 
	 * @return the parsed expression
	 */
	public static final Symbol[] parse(String input) {

		if (input.contains("@"))
			throw new IllegalArgumentException();

		input = input.replaceAll("\\s", "");

		ArrayList<Expression> replacedParentheticals = new ArrayList<>();

		// Finds all parenthetical subexpressions in the unparsed String expression.
		// Parentheses indicate either a predefined Shortcut, or grouping to change the
		// order of operations.
		while (input.indexOf('(') != -1)
			try {
				int start = -1;
				if ((start = input.indexOf('(', start + 1)) != -1) {

					int dif = 1;
					int end = start + 1;

					while (dif != 0)
						if (input.charAt(end) == ')')
							dif--;
						else if (input.charAt(end++) == '(')
							dif++;

					int function_call_start = start - 1;

					try {
						while (Variable.isAcceptableName(input.charAt(function_call_start)))
							function_call_start--;
					} catch (StringIndexOutOfBoundsException e) {
						// A StringIndexOutOfBoundsException may be thrown if the start of the function
						// call is at the start of the function, making it an expected exception to
						// receive.
					}
					function_call_start++;

					// All shortcut call args are passed by putting them into parentheses directly
					// following the shortcut's name, which emulates java static method calls.
					if (function_call_start == start) {
						replacedParentheticals.add(simplify(parse(input.substring(start + 1, end))));
						input = input.replaceFirst(Pattern.quote(input.substring(start, end + 1)), "@");
					} else {

						Shortcut s = SHORTCUTS.get(input.substring(function_call_start, start));

						Objects.requireNonNull(s);

						replacedParentheticals.add(s.apply(Arrays.stream(input.substring(start + 1, end).split(","))
								.map(Simplifier::parse).map(Simplifier::simplify).toArray(Expression[]::new)));

						input = input.replaceFirst(Pattern.quote(input.substring(function_call_start, end + 1)), "@");
					}
				}
			} catch (StringIndexOutOfBoundsException e) {
				throw new IllegalArgumentException(e);
			}

		ArrayList<Object> expression = new ArrayList<>(input.length());

		for (Character c : input.toCharArray())
			expression.add(c.toString());

		for (Expression o : replacedParentheticals) {
			int lastfillerindex = expression.indexOf("@");
			expression.set(lastfillerindex, o);
			lastfillerindex = expression.subList(lastfillerindex + 1, expression.size()).indexOf("@");
		}

		expression.replaceAll(o -> {

			if (!(o instanceof String))
				return o;

			switch ((String) o) {
			case "+":
				return Operator.ADD;
			case "-":
				return Operator.SUBTRACT;
			case "*":
				return Operator.MULTIPLY;
			case "/":
				return Operator.DIVIDE;
			case "^":
				return Operator.POW;
			case "âˆš":
				return Operator.ROOT;
			default:
				return o;
			}
		});

		// Combined numbers with adjacent number characters to form large consecutive
		// numeric Strings
		boolean found;
		do {
			found = false;

			for (int i = 0; i < expression.size() - 1; i++)
				if (expression.get(i)instanceof String exi && expression.get(i + 1)instanceof String exi1
						&& isValidNumber(exi) && isValidNumber(exi1)) {
					expression.set(i, exi + exi1);
					expression.remove(i + 1);
					found = true;
				}

		} while (found);

		// Parses numbers into Decimals
		for (int i = 0; i < expression.size(); i++)
			try {
				expression.set(i, parse(new BigDecimal((String) expression.get(i))));
			} catch (ClassCastException | NumberFormatException e) {
			}

		do {
			found = false;

			for (int i = 0; i < expression.size() - 1; i++)
				if (expression.get(i)instanceof String exi && expression.get(i + 1)instanceof String exi1
						&& Variable.isAcceptableName(exi) && Variable.isAcceptableName(exi1)) {
					expression.set(i, exi + exi1);
					expression.remove(i + 1);
					found = true;
				}

		} while (found);

		for (int i = 0; i < expression.size(); i++)
			try {
				expression.set(i, new Variable((String) expression.get(i)));
			} catch (ClassCastException c) {
			}

		// If there is a number and a variable next to each other, insert a
		// multiplication sign
		// EG 4x -> 4*x
		for (int i = 0; i < expression.size() - 1; i++)
			if (expression.get(i)instanceof Expression exi && expression.get(i + 1)instanceof Expression exi1)
				expression.add(i + 1, Operator.MULTIPLY);

		expression.forEach(o -> {
			if (!(o instanceof Symbol))
				throw new IllegalArgumentException();
		});

		for (int i = 0; i < expression.size(); i++)
			if (expression.get(i) == Operator.SUBTRACT && (i == 0 || expression.get(i - 1) instanceof Operator)
					&& expression.get(i + 1)instanceof Expression exp) {
				expression.set(i, exp.negate());
				expression.remove(i + 1);
			}

		return expression.toArray(new Symbol[expression.size()]);
	}

	/*
	 * Utility methods for parser.
	 */

	private static final boolean isValidNumber(String s) {
		for (char c : s.toCharArray())
			if (!NUMBER_CHARACTER.test(c))
				return false;
		return true;
	}

	/**
	 * Adds the shortcut to the registry of shortcuts under the specified call
	 * String. If a shortcut is already defined under the same call String, return
	 * false.
	 * 
	 * @param call
	 * @param action
	 * @return whether the following call String has been registered to this action.
	 */
	public static final boolean define(String call, Shortcut action) {
		if (SHORTCUTS.containsKey(call))
			return false;
		else {
			synchronized (lock) {
				SHORTCUTS.put(call, action);
			}
			return true;
		}
	}

	public static synchronized final boolean undefine(String call) {
		return SHORTCUTS.remove(call) != null;
	}

	private Simplifier() {
		throw new UnsupportedOperationException();
	}
}
