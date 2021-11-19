package soluturus.base.internal;

import soluturus.base.expressions.Expression;
import soluturus.base.internal.algebraic.Product;

public final class SoluturusMath {

	// Prevents instantiation
	private SoluturusMath() {
		throw new Error();
	}

	static boolean canMultiply(Expression m1, Expression m2) {
		if (m1.equals(Expression.zero) || m2.equals(Expression.zero) || m1.equals(Expression.one)
				|| m2.equals(Expression.one))
			return true;
		else
			return !(m1.multiply(m2) instanceof Product);
	}

	static boolean canDivide(Expression dividend, Expression divisor) {
		return false;
	}
}
