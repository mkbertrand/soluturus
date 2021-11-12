package soluturus.base.algebraic;

import soluturus.base.ExpressionContainmentException;
import soluturus.base.exposed.Expression;
import soluturus.base.internal.SoluturusAddition;
import soluturus.base.internal.SoluturusMultiplication;

public final record Sum(Expression[] addends) implements Expression {

	public Sum(Expression... addends) {

		if (addends.length < 2)
			throw new ExpressionContainmentException();

		for (Expression e : addends)
			if (e instanceof Sum)
				throw new ExpressionContainmentException();
		this.addends = addends;
	}

	public final Expression[] addends() {
		return addends.clone();
	}

	public final int length() {
		return addends.length;
	}

	@Override
	public Expression add(Expression addend) {
		if (addend instanceof Integer a2)
			return SoluturusAddition.add(a2, this);
		else if (addend instanceof Variable a2)
			return SoluturusAddition.add(a2, this);
		else
			return SoluturusAddition.add(this, addend);
	}

	@Override
	public Expression multiply(Expression multiplicand) {
		if (multiplicand instanceof Integer mu2)
			return SoluturusMultiplication.multiply(mu2, this);
		else if (multiplicand instanceof Variable mu2)
			return SoluturusMultiplication.multiply(mu2, this);
		else
			return SoluturusMultiplication.multiply(this, multiplicand);
	}

	@Override
	public Expression negate() {
		Expression[] result = new Expression[addends.length];
		for (int i = 0; i < addends.length; i++)
			result[i] = addends[i].negate();
		return new Sum(result);
	}

	@Override
	public Expression reciprocate() {
		return new Power(this, negative_one);
	}

	@Override
	public final boolean isKnown() {
		for (Expression e : addends)
			if (!e.isKnown())
				return false;
		return true;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder(addends[0].toString());

		String end;
		for (int i = 1; i < addends.length; i++)
			sb.append((end = addends[i].toString()).charAt(0) == '-' ? end : "+" + end);

		return sb.toString();
	}
}
