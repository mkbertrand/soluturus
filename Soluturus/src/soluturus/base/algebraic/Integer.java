package soluturus.base.algebraic;

import java.math.BigInteger;

import soluturus.base.ZeroDivisionException;
import soluturus.base.exposed.Expression;
import soluturus.base.internal.SoluturusAddition;
import soluturus.base.internal.SoluturusMath;
import soluturus.base.internal.SoluturusMultiplication;

public final record Integer(BigInteger number) implements Expression {

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
}
