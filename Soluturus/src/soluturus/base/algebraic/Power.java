package soluturus.base.algebraic;

import soluturus.base.exposed.Expression;
import soluturus.base.internal.SoluturusAddition;
import soluturus.base.internal.SoluturusMultiplication;

public final record Power(Expression base, Expression exponent) implements Expression {

	@Override
	public Expression add(Expression addend) {

		if (addend instanceof Integer a2)
			return SoluturusAddition.add(a2, this);
		else if (addend instanceof Variable a2)
			return SoluturusAddition.add(a2, this);
		else if (addend instanceof Sum a2)
			return SoluturusAddition.add(a2, this);
		else if (addend instanceof Product a2)
			return SoluturusAddition.add(a2, this);
		else
			return SoluturusAddition.add(this, addend);
	}

	@Override
	public Expression multiply(Expression multiplicand) {
		if (multiplicand instanceof Integer multiplicand0)
			return SoluturusMultiplication.multiply(multiplicand0, this);
		return SoluturusMultiplication.multiply(this, multiplicand);
	}

	@Override
	public Expression negate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression reciprocate() {
		if (exponent.equals(negative_one))
			return base;
		else if (exponent instanceof Integer exponent0 && exponent0.number().signum() == -1)
			return new Power(base, exponent.negate());
		return base.pow(exponent.negate());
	}

	@Override
	public final boolean isKnown() {
		return base.isKnown() && exponent.isKnown();
	}

	@Override
	public String toString() {
		// TODO
		return base + "^" + exponent;
	}
}
