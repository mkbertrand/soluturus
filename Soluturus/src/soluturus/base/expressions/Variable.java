package soluturus.base.expressions;

import soluturus.base.internal.InternalAddition;
import soluturus.base.internal.InternalDivision;
import soluturus.base.internal.InternalExponentiation;
import soluturus.base.internal.InternalMultiplication;
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
public final record Variable(String name) implements Expression {

	public static final boolean isAcceptableName(char c) {
		return Character.getType(c) == Character.CURRENCY_SYMBOL || Character.isAlphabetic(c) || c == '_';
	}

	public static final boolean isAcceptableName(String s) {
		for (char c : s.toCharArray())
			if (!isAcceptableName(c) || c == '(' || c == ')' || c == '\'')
				return false;
		return true;
	}

	public Variable(char name) {
		this(Character.toString(name));
	}

	public Variable(String name) {
		if (!isAcceptableName(name))
			throw new IllegalArgumentException();
		this.name = name;
	}

	@Override
	public Variable clone() {
		return new Variable(name);
	}

	@Override
	public Expression add(Expression addend) {
		if (addend instanceof Integer a2)
			return InternalAddition.add(a2, this);
		else
			return InternalAddition.add(this, addend);
	}

	@Override
	public Expression multiply(Expression multiplicand) {
		if (multiplicand instanceof Integer mu2)
			return InternalMultiplication.multiply(mu2, this);
		else
			return InternalMultiplication.multiply(this, multiplicand);
	}

	@Override
	public Expression divide(Expression divisor) {
		return InternalDivision.divide(this, divisor);
	}

	@Override
	public Expression pow(Expression exponent) {
		return InternalExponentiation.pow(this, exponent);
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
	public Expression sin() {
		if (this.equals(pi))
			return zero;
		else
			return new Sine(this);
	}

	@Override
	public Expression substitute(Variable v, Expression replacement) {
		if (equals(v))
			return replacement;
		else
			return this;
	}

	@Override
	public Integer derive(Variable v) {
		return equals(v) ? one : zero;
	}

	@Override
	public Variable[] factor() {
		return new Variable[] { this };
	}

	@Override
	public final boolean isKnown() {
		return false;
	}

	@Override
	public boolean isMonomial() {
		return true;
	}

	@Override
	public boolean isPolynomial() {
		return false;
	}

	@Override
	public String toString() {
		return name;
	}
}
