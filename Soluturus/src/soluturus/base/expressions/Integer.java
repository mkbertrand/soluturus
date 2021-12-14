package soluturus.base.expressions;

import java.math.BigInteger;

import soluturus.base.exceptions.ZeroDivisionException;
import soluturus.base.internal.InternalAddition;
import soluturus.base.internal.InternalDivision;
import soluturus.base.internal.InternalExponentiation;
import soluturus.base.internal.InternalMultiplication;
import soluturus.base.internal.algebraic.Logarithm;
import soluturus.base.internal.algebraic.Power;
import soluturus.base.internal.trigonometric.Sine;
import soluturus.calculations.IntegerUtils;

/**
 * Represents a known integer value within an Expression or as a self contained
 * Expression.
 * 
 * @author Miles K. Bertrand
 * 
 */
public final record Integer(BigInteger number) implements Expression, Comparable<Integer> {

	public static Integer of(int number) {
		return new Integer(number);
	}

	public static Integer of(long number) {
		return new Integer(number);
	}

	public static Integer of(BigInteger number) {
		return new Integer(number);
	}

	public Integer(long number) {
		this(BigInteger.valueOf(number));
	}

	public int signum() {
		return number.signum();
	}

	@Override
	public int compareTo(Integer o) {
		return number.compareTo(o.number);
	}

	public Integer abs() {
		return new Integer(number.abs());
	}

	@Override
	public Integer clone() {
		return new Integer(number);
	}

	@Override
	public Expression add(Expression addend) {
		return InternalAddition.add(this, addend);
	}

	@Override
	public Expression multiply(Expression multiplicand) {
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
		// TODO
		return new Logarithm(base, this);
	}

	@Override
	public Integer negate() {
		return new Integer(number.negate());
	}

	@Override
	public Expression reciprocate() {
		if (number.equals(BigInteger.ZERO))
			throw new ZeroDivisionException();
		else if (number.equals(BigInteger.ONE))
			return this;
		else if (equals(negative_one))
			return this;
		else
			return new Power(this, negative_one);
	}

	@Override
	public Expression sin() {
		if (this.equals(zero))
			return zero;
		else if (number.signum() < 0)
			return new Sine(negate()).negate();
		else
			return new Sine(this);
	}

	@Override
	public Integer derive(Variable v) {
		return zero;
	}
	
	@Override
	public Integer substitute(Variable v, Expression replacement) {
		return this;
	}

	@Override
	public Integer[] factor() {

		BigInteger[] bfactors = IntegerUtils.factor(number);
		Integer[] factors = new Integer[signum() < 0 ? bfactors.length + 1 : bfactors.length];
		if (signum() < 0)
			factors[bfactors.length] = negative_one;
		for (int i = 0; i < bfactors.length; i++)
			factors[i] = new Integer(bfactors[i]);
		return factors;
	}

	@Override
	public boolean isKnown() {
		return true;
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
	public boolean isFraction() {
		return true;
	}
	
	@Override
	public String toString() {
		return number.toString();
	}
}
