package soluturus.utils.internal;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import soluturus.base.Trigonometric;
import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Integer;
import soluturus.base.expressions.Variable;

/**
 * The Simplifier converts strings and other formats into operable expressions.
 * The source code for this project was lifted directly from the previous Lambda
 * project with slight modifications made.
 * 
 * @author Miles K. Bertrand
 * 
 */
public final class ExpressionParser {

	private static final HashMap<String, Character> greekletters;
	private static final HashMap<String, Function<Expression[], Expression>> functions;

	static {
		greekletters = new HashMap<>(Map.ofEntries(new SimpleEntry<>("Alpha", '\u0391'),
				new SimpleEntry<>("alpha", '\u03B1'), new SimpleEntry<>("Beta", '\u0392'),
				new SimpleEntry<>("beta", '\u03B2'), new SimpleEntry<>("Gamma", '\u0393'),
				new SimpleEntry<>("gamma", '\u03B3'), new SimpleEntry<>("Delta", '\u0394'),
				new SimpleEntry<>("delta", '\u03B4'), new SimpleEntry<>("Epsilon", '\u0395'),
				new SimpleEntry<>("epsilon", '\u03B5'), new SimpleEntry<>("Zeta", '\u0396'),
				new SimpleEntry<>("zeta", '\u03B6'), new SimpleEntry<>("Eta", '\u0397'),
				new SimpleEntry<>("eta", '\u03B7'), new SimpleEntry<>("Heta", '\u0397'),
				new SimpleEntry<>("heta", '\u03B7'), new SimpleEntry<>("Theta", '\u0398'),
				new SimpleEntry<>("theta", '\u03B8'), new SimpleEntry<>("Iota", '\u0399'),
				new SimpleEntry<>("iota", '\u03B9'), new SimpleEntry<>("Kappa", '\u039A'),
				new SimpleEntry<>("kappa", '\u03BA'), new SimpleEntry<>("Lambda", '\u039B'),
				new SimpleEntry<>("lambda", '\u03BB'), new SimpleEntry<>("Mu", '\u039C'),
				new SimpleEntry<>("mu", '\u03BC'), new SimpleEntry<>("Nu", '\u039D'), new SimpleEntry<>("nu", '\u03BD'),
				new SimpleEntry<>("Xi", '\u039E'), new SimpleEntry<>("xi", '\u03BE'),
				new SimpleEntry<>("Omicron", '\u039F'), new SimpleEntry<>("omicron", '\u03BF'),
				new SimpleEntry<>("Pi", '\u03A0'), new SimpleEntry<>("pi", '\u03C0'),
				new SimpleEntry<>("Rho", '\u03A1'), new SimpleEntry<>("rho", '\u03C1'),
				new SimpleEntry<>("Sigma", '\u03A3'), new SimpleEntry<>("sigma", '\u03C3'),
				new SimpleEntry<>("Tau", '\u03A4'), new SimpleEntry<>("tau", '\u03C4'),
				new SimpleEntry<>("Upsilon", '\u03A5'), new SimpleEntry<>("upsilon", '\u03C5'),
				new SimpleEntry<>("Phi", '\u03A6'), new SimpleEntry<>("phi", '\u03C6'),
				new SimpleEntry<>("Chi", '\u03A7'), new SimpleEntry<>("chi", '\u03C7'),
				new SimpleEntry<>("Psi", '\u03A8'), new SimpleEntry<>("psi", '\u03C8'),
				new SimpleEntry<>("Omega", '\u03A9'), new SimpleEntry<>("omega", '\u03C9')));

		functions = new HashMap<>(Map.of("sin", e -> Trigonometric.sin(e[0]), "ln", e -> Expression.ln(e[0]), "log",
				e -> e[1].log(e[0]), "root", e -> e.length == 1 ? e[0].root(Expression.one_half) : e[0].root(e[1])));
	}

	private static final Expression parse(BigDecimal b) {
		if (b.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0)
			return new Integer(b.toBigIntegerExact());
		else {
			BigDecimal pow = BigDecimal.TEN.pow(b.toString().length() - 1 - b.toString().indexOf('.'));
			return new Integer(b.multiply(pow).toBigIntegerExact()).divide(new Integer(pow.toBigIntegerExact()));
		}
	}

	private static final boolean isNumberCharacter(String s) {
		for (char c : s.toCharArray())
			if (Character.getType(c) != Character.DECIMAL_DIGIT_NUMBER && c != '.' && c != ',')
				return false;
		return true;
	}

	// Returns whether this String represents an operator
	private static boolean isOperator(String s) {
		return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/") || s.equals("^");
	}

	/**
	 * Through strange and unholy mechanisms, this method converts a string into an
	 * Expression.
	 * 
	 * @param input
	 * @return an Expression
	 */
	public static final Expression parse(String input) {

		input = input.replaceAll("\\s", "");

		// The results of the parenthetical expressions and functions in order.
		ArrayList<Expression> replacedParentheticals = new ArrayList<>();

		// Finds all parenthetical subexpressions in the unparsed String expression.
		// Parentheses indicate either a predefined function (such as sin(x)), or
		// grouping to change the
		// order of operations.
		while (input.indexOf('(') != -1) {

			int openIndex = -1;
			if ((openIndex = input.indexOf('(', openIndex + 1)) != -1) {

				// Finds the matching closing parenthesis to the opening. Since there can be
				// multiple layers of parentheses, the correct one is found by counting up for
				// each opening and down for each closing.
				int openParDif = 1;
				int closeIndex = openIndex + 1;

				while (openParDif != 0) {
					if (input.charAt(closeIndex) == ')')
						openParDif--;
					else if (input.charAt(closeIndex) == '(')
						openParDif++;
					closeIndex++;
				}

				String inParentheses = input.substring(openIndex + 1, closeIndex - 1);

				int function_call_start = openIndex;

				while (function_call_start != 0 && Variable.isAcceptableName(input.charAt(function_call_start - 1)))
					function_call_start--;

				String replacePattern = Pattern.quote(input.substring(function_call_start, closeIndex));

				// All shortcut call arguments are passed by putting them into parentheses
				// directly
				// following the shortcut's name, which emulates java static method calls.
				if (function_call_start == openIndex) {
					replacedParentheticals.add(parse(inParentheses));
					input = input.replaceFirst(replacePattern, ")");
				} else {

					// Get function calls. Since some functions take more than one argument, the
					// string within the parentheses that contains the argument(s) must be split
					// into the arguments.
					String[] functionargumentstrings = inParentheses.split(",");
					Expression[] functionarguments = new Expression[functionargumentstrings.length];

					for (int i = 0; i < functionarguments.length; i++)
						functionarguments[i] = parse(functionargumentstrings[i]);

					Expression result = functions.get(input.substring(function_call_start, openIndex))
							.apply(functionarguments);

					replacedParentheticals.add(result);

					input = input.replaceFirst(replacePattern, ")");
				}
			}
		}

		// Converts this string into an arraylist of the constituent characters.
		ArrayList<Object> expression = new ArrayList<>(Arrays.asList(input.split("")));

		// Within the arraylist, replace the ')' character with the expression it
		// represented.
		for (Expression o : replacedParentheticals)
			expression.set(expression.indexOf(")"), o);

		// Combined numbers with adjacent number characters to form large consecutive
		// numeric Strings
		for (int i = 1; i < expression.size(); i++)
			if (expression.get(i - 1)instanceof String str1 && expression.get(i)instanceof String str2
					&& isNumberCharacter(str1) && isNumberCharacter(str2)) {
				expression.set(i - 1, str1 + str2);
				expression.remove(i--);
			}

		boolean found;

		// Parses numbers into Decimals
		for (int i = 0; i < expression.size(); i++)
			if (expression.get(i)instanceof String expri)
				try {
					expression.set(i, parse(new BigDecimal(expri.replaceAll(",", ""))));
				} catch (NumberFormatException e) {
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

		// Parses strings into Variables, with English names for Greek letters being
		// replaced with the Greek letters referenced.
		for (int i = 0; i < expression.size(); i++)
			if (expression.get(i)instanceof String variable && !isOperator(variable))
				if (greekletters.containsKey(variable))
					expression.set(i, new Variable(Character.toString(greekletters.get(variable))));
				else if (variable.equals("i"))
					expression.set(i, Expression.i);
				else
					expression.set(i, new Variable(variable));

		// If there is a number and a variable next to each other, insert a
		// multiplication sign
		// EG 4x -> 4*x
		for (int i = 0; i < expression.size() - 1; i++)
			if (expression.get(i)instanceof Expression exi && expression.get(i + 1)instanceof Expression exi1)
				expression.add(i + 1, "*");

		for (int i = 0; i < expression.size(); i++)
			if (expression.get(i).equals("-") && (i == 0 || expression.get(i - 1)instanceof String i1 && isOperator(i1))
					&& expression.get(i + 1)instanceof Expression exp) {
				expression.set(i, exp.negate());
				expression.remove(i + 1);
			}

		int index;
		while ((index = expression.indexOf("^")) != -1) {
			expression.set(index - 1,
					((Expression) expression.get(index - 1)).pow((Expression) expression.remove(index + 1)));
			expression.remove(index);
		}

		// The first and last elements in an expression must be operands,
		// that is to say numbers, so they needn't be checked for whether they are a
		// type of operand.
		for (int i = 1; i < expression.size() - 1; i++)
			if (expression.get(i).equals("*")) {
				expression.set(i - 1,
						((Expression) expression.get(i - 1)).multiply((Expression) expression.remove(i + 1)));
				expression.remove(i--);
			} else if (expression.get(i).equals("/")) {
				expression.set(i - 1,
						((Expression) expression.get(i - 1)).divide((Expression) expression.remove(i + 1)));
				expression.remove(i--);
			}

		for (int i = 1; i < expression.size() - 1; i++)
			if (expression.get(i).equals("+")) {
				expression.set(i - 1, ((Expression) expression.get(i - 1)).add((Expression) expression.remove(i + 1)));
				expression.remove(i--);
			} else if (expression.get(i).equals("-")) {
				expression.set(i - 1,
						((Expression) expression.get(i - 1)).subtract((Expression) expression.remove(i + 1)));
				expression.remove(i--);
			}

		// If all goes well, the result should have a size() of one. If not, the user
		// passed a bad expression.
		if (expression.size() != 1)
			throw new IllegalArgumentException();

		return (Expression) expression.get(0);
	}
}
