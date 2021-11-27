package soluturus.base.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;

import soluturus.base.exceptions.ZeroDivisionException;
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
			return new Power(base, new Product(finexp));
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

	public static Expression pow(Integer b, Power e) {
		if (b.equals(Expression.zero))
			if (e.base()instanceof Integer ebint && ebint.signum() < 0)
				throw new ZeroDivisionException();
			else
				return Expression.zero;
		else if (b.equals(Expression.one))
			return Expression.one;
		else if (e.isFraction()) {

			Integer deg = (Integer) e.base();

			// Stores whether the radicand is negative while it is being operated on by
			// methods that require positive numbers.
			boolean negative = false;
			if (b.signum() < 0) {
				negative = true;
				b = b.abs();
			}

			// Stores whether the base of the exponent is negative.
			boolean recip = false;
			if (((Integer) e.base()).signum() < 0) {
				recip = true;
				deg = deg.abs();
			}

			BigInteger remainingDegree = BigInteger.ONE;

			BigInteger ret = b.number();

			for (BigInteger degree : IntegerUtils.factor(deg.number())) {

				BigDecimal radicand = new BigDecimal(ret);

				MathContext mc = new MathContext(
						-ExponentiationUtils.root(radicand, degree, new MathContext(1)).scale() + 3);

				BigDecimal root = ExponentiationUtils.root(radicand, degree, mc);

				if (root.toString().indexOf('.') != -1 && !(root.toString().indexOf('.') == root.toString().length() - 1
						|| root.toString().charAt(root.toString().indexOf('.')) == '0'))
					remainingDegree = remainingDegree.multiply(degree);
				else
					ret = root.toBigInteger();
			}

			Integer base = new Integer(negative ? ret.negate() : ret);
			Integer exponent = new Integer(recip ? remainingDegree.negate() : remainingDegree);

			if (exponent.equals(Expression.one))
				return base;
			else if (exponent.equals(Expression.negative_one))
				return base.reciprocate();
			else
				return new Power(base, exponent.reciprocate());
		} else
			return null;
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
