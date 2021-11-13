package soluturus.base.algebraic;

import soluturus.base.exposed.Expression;
import soluturus.base.internal.SoluturusAddition;
import soluturus.base.internal.SoluturusMath;
import soluturus.base.internal.SoluturusMultiplication;

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
	public Expression divide(Expression divisor) {
		return SoluturusMath.divide(this, divisor);
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
