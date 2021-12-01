package soluturus.base.expressions;

import java.math.BigInteger;

import soluturus.base.exceptions.ZeroDivisionException;
import soluturus.base.internal.SoluturusAddition;
import soluturus.base.internal.SoluturusDivision;
import soluturus.base.internal.SoluturusExponentiation;
import soluturus.base.internal.SoluturusMultiplication;
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
		return SoluturusAddition.add(this, addend);
	}

	@Override
	public Expression multiply(Expression multiplicand) {
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
	public Integer negate() {
		return new Integer(number.negate());
	}

	@Override
	public Expression reciprocate() {
		if (number.equals(BigInteger.ZERO))
			throw new ZeroDivisionException();
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
	public String toString() {
		return number.toString();
	}
}
