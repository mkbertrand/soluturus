package soluturus.base.internal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import soluturus.base.ZeroDivisionException;
import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Integer;
import soluturus.base.expressions.Variable;
import soluturus.base.internal.algebraic.Power;
import soluturus.base.internal.algebraic.Product;
import soluturus.base.internal.algebraic.Sum;
import soluturus.calculations.ExponentiationUtils;
import soluturus.calculations.IntegerUtils;

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

	public static Expression pow(Sum base, Expression exponent) {
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

	public static Expression pow(Power base, Expression exponent) {
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

	private static Expression product_exponent_pow(Expression base, Product exponent) {

		ArrayList<Expression> finexp = new ArrayList<>(Arrays.asList(exponent.factors()));
		for (int i = 0; i < finexp.size(); i++)
			if (SoluturusMath.canPow(base, finexp.get(i)))
				base = base.pow(finexp.remove(i--));

		if (finexp.size() == 0)
			return base;
		else if (finexp.size() == 1)
			return new Power(base, finexp.get(0));
		else {
			// TODO
			for (int i = 0; i < finexp.size(); i++)
				finexp.set(i, new Power(base, finexp.get(i)));

			return new Product(finexp);
		}
	}

	// TODO check for completion
	public static Expression pow(Integer b, Integer e) {
		if (b.equals(Expression.zero) && e.signum() < 0)
			throw new ZeroDivisionException();
		else if (b.equals(Expression.zero))
			return Expression.zero;
		if (e.signum() < 0)
			return Integer.of(ExponentiationUtils.pow(b.number(), (e.number().abs()))).reciprocate();
		else if (e.equals(Expression.zero))
			return Expression.one;
		else
			return Integer.of(ExponentiationUtils.pow(b.number(), e.number()));
	}

	public static Expression pow(Integer b, Variable e) {
		if (b.equals(Expression.zero))
			return Expression.zero;
		else
			return new Power(b, e);
	}

	public static Expression pow(Integer b, Product e) {
		return product_exponent_pow(b, e);
	}

	public static Expression pow(Variable b, Integer e) {
		if (e.equals(Expression.zero))
			return Expression.one;
		else if (e.equals(Expression.one))
			return b;
		else
			return new Power(b, e);
	}

	public static Expression pow(Variable b, Variable e) {
		return new Power(b, e);
	}

	public static Expression pow(Variable b, Product e) {
		return product_exponent_pow(b, e);
	}

	public static Expression pow(Variable b, Power e) {
		// TODO
		return new Power(b, e);
	}

	public static Expression pow(Sum b, Integer e) {

		if (e.signum() == 0)
			return Expression.one;
		else if (e.signum() < 0)
			return b.pow(e.negate()).reciprocate();
		else if (b.length() == 2) {
			Expression product = Expression.zero;

			final BigInteger n = e.number();
			final Expression x = b.addends()[0];
			final Expression y = b.addends()[1];

			for (BigInteger k = BigInteger.ZERO; k.compareTo(n) < 1; k = k.add(BigInteger.ONE))
				product = product.add(new Integer(IntegerUtils.nCk(n, k)).multiply(x.pow(new Integer(n.subtract(k))),
						y.pow(new Integer(k))));
			return product;
		} else {

			Expression base = b;
			BigInteger exponent = e.number();
			Expression result = Expression.one;

			while (exponent.signum() == 1) {
				if (exponent.testBit(0))
					result = result.multiply(base);
				base = base.multiply(base);
				exponent = exponent.shiftRight(1);
			}
			return result;

			// TODO
		}
	}

	public static Expression pow(Sum b, Product e) {
		return product_exponent_pow(b, e);
	}

	public static Expression pow(Product b, Product e) {
		return product_exponent_pow(b, e);
	}

	public static Expression pow(Power b, Integer e) {
		return b.base().pow(b.exponent().multiply(e));
	}

	public static Expression pow(Power b, Product e) {
		return product_exponent_pow(b, e);
	}

	public static Expression pow(Power b, Power e) {
		return b.base().pow(b.exponent().multiply(e));
	}
}
