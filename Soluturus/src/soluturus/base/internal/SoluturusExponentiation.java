package soluturus.base.internal;

import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Integer;
import soluturus.base.expressions.Variable;
import soluturus.base.internal.algebraic.Power;
import soluturus.base.internal.algebraic.Product;
import soluturus.base.internal.algebraic.Sum;
import soluturus.calculations.ExponentiationUtils;

public final class SoluturusExponentiation {

	private SoluturusExponentiation() {
		throw new UnsupportedOperationException();
	}

	public static Expression pow(Integer base, Expression exponent) {
		if (exponent instanceof Integer exponent0)
			return pow(base, exponent0);
		else if (exponent instanceof Variable exponent0)
			return pow(base, exponent0);
		else if (exponent instanceof Sum exponent0)
			return pow(base, exponent0);
		else if (exponent instanceof Product exponent0)
			return pow(base, exponent0);
		else if (exponent instanceof Power exponent0)
			return pow(base, exponent0);
		return null;
	}

	public static Expression pow(Variable base, Expression exponent) {
		if (exponent instanceof Integer exponent0)
			return pow(base, exponent0);
		else if (exponent instanceof Variable exponent0)
			return pow(base, exponent0);
		else if (exponent instanceof Sum exponent0)
			return pow(base, exponent0);
		else if (exponent instanceof Product exponent0)
			return pow(base, exponent0);
		else if (exponent instanceof Power exponent0)
			return pow(base, exponent0);
		return null;
	}

	public static Expression pow(Product base, Expression exponent) {
		if (exponent instanceof Integer exponent0)
			return pow(base, exponent0);
		else if (exponent instanceof Variable exponent0)
			return pow(base, exponent0);
		else if (exponent instanceof Sum exponent0)
			return pow(base, exponent0);
		else if (exponent instanceof Product exponent0)
			return pow(base, exponent0);
		else if (exponent instanceof Power exponent0)
			return pow(base, exponent0);
		return null;
	}

	// TODO
	public static Expression pow(Integer b, Integer e) {
		if (e.signum() == -1)
			return Integer.of(ExponentiationUtils.pow(b.number(), e.number().abs())).reciprocate();
		return Integer.of(ExponentiationUtils.pow(b.number(), e.number()));
	}
}
