package soluturus.base.expressions;

import java.math.BigInteger;

import soluturus.base.ZeroDivisionException;
import soluturus.base.internal.SoluturusAddition;
import soluturus.base.internal.SoluturusMath;
import soluturus.base.internal.SoluturusMultiplication;
import soluturus.base.internal.algebraic.Power;
import soluturus.base.internal.trigonometric.Sine;

/**
 * Represents a known integer value within an Expression or as a self contained
 * Expression.
 * 
 * @author Miles K. Bertrand
 * 
 */
public final record Integer(BigInteger number) implements Expression, Comparable<Integer> {

	public Integer(long number) {
		this(BigInteger.valueOf(number));
	}

	public static final Integer of(int number) {
		return new Integer(number);
	}

	public static final Integer of(long number) {
		return new Integer(number);
	}

	public static final Integer of(BigInteger number) {
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
	public Expression sin() {
		if (this.equals(zero))
			return zero;
		else if (number.signum() < 0)
			return new Sine(negate()).negate();
		else
			return new Sine(this);
	}

	@Override
	public Expression divide(Expression divisor) {
		return SoluturusMath.divide(this, divisor);
	}

	@Override
	public Integer negate() {
		return new Integer(number.negate());
	}

	@Override
	public Expression reciprocate() {
		if (number.equals(BigInteger.ZERO))
			throw new ZeroDivisionException();
		return new Power(this, negative_one);
	}

	@Override
	public final boolean isKnown() {
		return true;
	}

	@Override
	public String toString() {
		return number.toString();
	}

	@Override
	public int compareTo(Integer o) {
		return number.compareTo(o.number);
	}
}
