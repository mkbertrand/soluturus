package soluturus.base.internal;

import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Integer;
import soluturus.base.expressions.Variable;
import soluturus.base.internal.algebraic.Power;
import soluturus.base.internal.algebraic.Product;
import soluturus.base.internal.algebraic.Sum;

final class SoluturusCanAdd {

	private SoluturusCanAdd() {
		throw new UnsupportedOperationException();
	}

	static boolean canAdd(Expression a1, Expression a2) {
		if (a1.equals(a2))
			return true;
		else if (a1 instanceof Integer a1i)
			return canAdd(a1i, a2);
		else if (a2 instanceof Integer a2i)
			return canAdd(a2i, a1);
		else if (a1 instanceof Sum || a2 instanceof Sum)
			return false;
		else if (a1 instanceof Product a1p)
			return canAdd(a1p, a2);
		else if (a2 instanceof Product a2p)
			return canAdd(a2p, a1);
		return bareCanAdd(a1, a2);
	}

	static boolean canAdd(Integer a1, Expression a2) {
		if (a1.equals(Expression.zero))
			return true;
		else if (a2 instanceof Integer)
			return true;
		else if (a2 instanceof Variable)
			return false;
		else if (a2 instanceof Power a2pow && a2pow.base() instanceof Integer && a2pow.exponent() instanceof Integer)
			return true;
		else if (!a2.isKnown())
			return false;
		else
			return false;
	}

	static boolean canAdd(Product a1, Expression a2) {
		if (a2 instanceof Product)
			return bareCanAdd(a1, a2);
		else
			// TODO
			return false;
	}

	private static boolean bareCanAdd(Expression a1, Expression a2) {
		return !(a1.add(a2) instanceof Sum);
	}
}
