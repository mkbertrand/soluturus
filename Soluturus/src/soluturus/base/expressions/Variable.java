package soluturus.base.expressions;

import soluturus.base.internal.SoluturusAddition;
import soluturus.base.internal.SoluturusDivision;
import soluturus.base.internal.SoluturusExponentiation;
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
	public Expression divide(Expression divisor) {
		return SoluturusDivision.divide(this, divisor);
	}

	@Override
	public Expression pow(Expression exponent) {
		return SoluturusExponentiation.pow(this, exponent);
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
