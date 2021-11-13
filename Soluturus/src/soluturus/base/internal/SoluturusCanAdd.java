package soluturus.base.internal;

import soluturus.base.algebraic.Integer;
import soluturus.base.algebraic.Power;
import soluturus.base.algebraic.Variable;
import soluturus.base.exposed.Expression;

final class SoluturusCanAdd {

	private SoluturusCanAdd() {
		throw new UnsupportedOperationException();
	}

	static boolean canAdd(Expression a1, Expression a2) {
		if (a1.equals(a2))
			return true;
		else if (a1 instanceof Integer)
			return canAdd((Integer) a1, a2);
		else if (a2 instanceof Integer)
			return canAdd((Integer) a2, a1);
		return false;
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
}
