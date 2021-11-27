package soluturus.utils.internal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
public final class Simplifier {

	private static final HashMap<String, Character> greekletters = new HashMap<String, Character>();
	private static final HashMap<String, Function<Expression[], Expression>> functions = new HashMap<String, Function<Expression[], Expression>>();

	static {
		greekletters.put("Alpha", '\u0391');
		greekletters.put("alpha", '\u03B1');
		greekletters.put("Beta", '\u0392');
		greekletters.put("beta", '\u03B2');
		greekletters.put("Gamma", '\u0393');
		greekletters.put("gamma", '\u03B3');
		greekletters.put("Delta", '\u0394');
		greekletters.put("delta", '\u03B4');
		greekletters.put("Epsilon", '\u0395');
		greekletters.put("epsilon", '\u03B5');
		greekletters.put("Zeta", '\u0396');
		greekletters.put("zeta", '\u03B6');
		greekletters.put("Eta", '\u0397');
		greekletters.put("eta", '\u03B7');
		greekletters.put("Theta", '\u0398');
		greekletters.put("theta", '\u03B8');
		greekletters.put("Iota", '\u0399');
		greekletters.put("iota", '\u03B9');
		greekletters.put("Kappa", '\u039A');
		greekletters.put("kappa", '\u03BA');
		greekletters.put("Lambda", '\u039B');
		greekletters.put("lambda", '\u03BB');
		greekletters.put("Mu", '\u039C');
		greekletters.put("mu", '\u03BC');
		greekletters.put("Nu", '\u039D');
		greekletters.put("nu", '\u03BD');
		greekletters.put("Xi", '\u039E');
		greekletters.put("xi", '\u03BE');
		greekletters.put("Omicron", '\u039F');
		greekletters.put("omicron", '\u03BF');
		greekletters.put("Pi", '\u03A0');
		greekletters.put("pi", '\u03C0');
		greekletters.put("Rho", '\u03A1');
		greekletters.put("rho", '\u03C1');
		greekletters.put("Sigma", '\u03A3');
		greekletters.put("sigma", '\u03C3');
		greekletters.put("Tau", '\u03A4');
		greekletters.put("tau", '\u03C4');
		greekletters.put("Upsilon", '\u03A5');
		greekletters.put("upsilon", '\u03C5');
		greekletters.put("Phi", '\u03A6');
		greekletters.put("phi", '\u03C6');
		greekletters.put("Chi", '\u03A7');
		greekletters.put("chi", '\u03C7');
		greekletters.put("Psi", '\u03A8');
		greekletters.put("psi", '\u03C8');
		greekletters.put("Omega", '\u03A9');
		greekletters.put("omega", '\u03C9');

		functions.put("sin", e -> Trigonometric.sin(e[0]));
		functions.put("ln", e -> e[0].ln());
		functions.put("log", e -> e[0].log(e[1]));
		functions.put("root", e -> e.length == 1 ? e[0].root(Expression.one_half) : e[0].root(e[1]));

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
			if (Character.getType(c) != Character.DECIMAL_DIGIT_NUMBER && c != '.')
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

			int start = -1;
			if ((start = input.indexOf('(', start + 1)) != -1) {

				// Finds the matching closing parenthesis to the opening. Since there can be
				// multiple layers of parentheses, the correct one is found by counting up for
				// each opening and down for each closing.
				int dif = 1;
				int end = start + 1;

				while (dif != 0) {
					if (input.charAt(end) == ')')
						dif--;
					else if (input.charAt(end) == '(')
						dif++;
					end++;
				}

				String inParentheses = input.substring(start + 1, end - 1);

				int function_call_start = start;

				while (function_call_start != 0 && Variable.isAcceptableName(input.charAt(function_call_start - 1)))
					function_call_start--;

				String replacePattern = Pattern.quote(input.substring(function_call_start, end));

				// All shortcut call args are passed by putting them into parentheses directly
				// following the shortcut's name, which emulates java static method calls.
				if (function_call_start == start) {
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

					Expression result = functions.get(input.substring(function_call_start, start))
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
		for (int i = 1; i < expression.size(); i++) {
			if (expression.get(i - 1)instanceof String str1 && expression.get(i)instanceof String str2
					&& isNumberCharacter(str1) && isNumberCharacter(str2)) {
				expression.set(i - 1, str1 + str2);
				expression.remove(i--);
			}
		}

		boolean found;

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
			if (expression.get(i)instanceof String variable && !isOperator(variable)) {
				if (greekletters.containsKey(variable))
					variable = Character.toString(greekletters.get(variable));
				expression.set(i, new Variable(variable));
			}

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
