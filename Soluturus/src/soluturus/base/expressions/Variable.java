package soluturus.base.expressions;

import soluturus.base.internal.SoluturusAddition;
import soluturus.base.internal.SoluturusMath;
import soluturus.base.internal.SoluturusMultiplication;
import soluturus.base.internal.algebraic.Power;
import soluturus.base.internal.algebraic.Product;
import soluturus.base.internal.trigonometric.Sine;

/**
 * Represents a variable whose value is unknown or not specified for the purpose
 * of a problem.
 * 
 * @author Miles K. Bertrand
 * 
 */
public record Variable(String name) implements Expression {

	public static final boolean isAcceptableName(char c) {
		return Character.getType(c) == Character.CURRENCY_SYMBOL || Character.isAlphabetic(c);
	}

	public static final boolean isAcceptableName(String s) {
		for (char c : s.toCharArray())
			if (!isAcceptableName(c) || c == '(' || c == ')' || c == '\'')
				return false;
		return true;
	}

	@Override
	public Expression add(Expression addend) {
		if (addend instanceof Integer a2)
			return SoluturusAddition.add(a2, this);
		else
			return SoluturusAddition.add(this, addend);
	}

	@Override
	public Expression multiply(Expression multiplicand) {
		if (multiplicand instanceof Integer mu2)
			return SoluturusMultiplication.multiply(mu2, this);
		else
			return SoluturusMultiplication.multiply(this, multiplicand);
	}

	@Override
	public Expression sin() {
		if (this.equals(pi))
			return zero;
		else
			return new Sine(this);
	}

	@Override
	public Expression divide(Expression divisor) {
		return SoluturusMath.divide(this, divisor);
	}

	@Override
	public Expression pow(Expression exponent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression log(Expression base) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression negate() {
		return new Product(negative_one, this);
	}

	@Override
	public Expression reciprocate() {
		return new Power(this, negative_one);
	}

	@Override
	public final boolean isKnown() {
		return false;
	}

	@Override
	public String toString() {
		return name;
	}
}
