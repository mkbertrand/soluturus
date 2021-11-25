package soluturus.base.internal;

import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Integer;
import soluturus.base.internal.algebraic.Power;
import soluturus.base.internal.algebraic.Product;

final class SoluturusMath {

	// Prevents instantiation
	private SoluturusMath() {
		throw new Error();
	}

	static Expression productRemove(Product p, int index) {

		Expression[] factors = p.factors();

		if (index >= p.length() || index < 0)
			throw new IndexOutOfBoundsException();
		else if (p.length() == 2)
			return index == 0 ? factors[1] : factors[0];
		else {
			Expression[] result = new Expression[factors.length - 1];
			System.arraycopy(factors, 0, result, 0, index);
			System.arraycopy(factors, index + 1, result, index, factors.length - index - 1);
			return new Product(result);
		}
	}

	static Expression removeConstant(Product prod) {
		int removeIndex = -1;
		for (int i = 0; i < prod.length(); i++)
			if (prod.factors()[i] instanceof Integer)
				removeIndex = i;
		if (removeIndex == -1)
			return prod;
		else
			return SoluturusMath.productRemove(prod, removeIndex);
	}
	
	static boolean canMultiply(Expression m1, Expression m2) {
		if (m1.equals(Expression.zero) || m2.equals(Expression.zero) || m1.equals(Expression.one)
				|| m2.equals(Expression.one))
			return true;
		else
			return !(m1.multiply(m2) instanceof Product);
	}

	static boolean canPow(Expression base, Expression exponent) {
		if (base instanceof Power bpow)
			// TODO
			return canPow(bpow.base(), exponent);
		else
			return !(base.pow(exponent) instanceof Power);
	}

	static boolean canDivide(Expression dividend, Expression divisor) {
		return false;
	}
}
