package soluturus.base.internal;

import java.math.BigInteger;

import soluturus.base.ZeroDivisionException;
import soluturus.base.algebraic.Integer;
import soluturus.base.algebraic.Product;
import soluturus.base.algebraic.Sum;
import soluturus.base.algebraic.Variable;
import soluturus.base.exposed.Expression;
import soluturus.calculations.ExponentiationUtils;
import soluturus.calculations.IntegerUtils;

public final class SoluturusMath {

	// Prevents instantiation
	private SoluturusMath() {
		throw new Error();
	}

	/*
	 * Routes operations to methods with math
	 */

	public static Expression divide(Integer dividend, Expression divisor) {
		if (divisor instanceof Integer castdivisor)
			return divide(dividend, castdivisor);
		if (divisor instanceof Variable castdivisor)
			return divide(dividend, castdivisor);
		return null;
	}

	public static Expression divide(Variable dividend, Expression divisor) {
		if (divisor instanceof Integer castdivisor)
			return divide(dividend, castdivisor);
		if (divisor instanceof Variable castdivisor)
			return divide(dividend, castdivisor);
		return null;
	}

	public static Expression divide(Product dividend, Expression divisor) {

		return null;
	}

	private static Expression product_divide(Product dividend, Expression divisor) {

		Expression[] factors = dividend.factors();

		for (int i = 0; i < factors.length; i++)
			if (SoluturusMath.canDivide(factors[i], divisor)) {
				Expression prod = factors[i].divide(divisor);

				if (factors.length == 2)
					return i == 0 ? factors[1].multiply(prod) : factors[0].multiply(prod);
				else {
					Expression[] result = new Expression[factors.length - 1];
					System.arraycopy(factors, 0, result, 0, i);
					System.arraycopy(factors, i + 1, result, i, factors.length - i - 1);
					return new Product(result).multiply(prod);
				}
			}

		Expression[] result = new Expression[factors.length + 1];
		System.arraycopy(factors, 0, result, 0, factors.length);
		result[factors.length] = divisor.reciprocate();
		return new Product(result);

	}

	/*
	 * Handles operations between two known types
	 */

	public static Expression divide(Integer dividend, Integer divisor) {

		if (divisor.equals(Expression.zero))
			throw new ZeroDivisionException();
		else if (dividend.equals(Expression.zero))
			return Expression.zero;
		boolean isNegative = false;

		if (dividend.number().signum() == -1 && divisor.number().signum() == -1) {
			dividend = dividend.negate();
			divisor = divisor.negate();
		} else if (isNegative = dividend.number().signum() == -1)
			dividend = dividend.negate();
		else if (isNegative = divisor.number().signum() == -1)
			divisor = divisor.negate();

		BigInteger gcd = IntegerUtils.gcd(dividend.number(), divisor.number());

		if (dividend.equals(Expression.one))
			return (isNegative ? divisor.negate() : divisor).reciprocate();
		else if (gcd.equals(divisor.number()))
			return new Integer((isNegative ? dividend.negate() : dividend).number().divide(gcd));
		else
			return new Product(new Integer((isNegative ? dividend.negate() : dividend).number().divide(gcd)),
					new Integer(divisor.number().divide(gcd)).reciprocate());
	}

	public static Expression divide(Integer dividend, Variable divisor) {

		if (dividend.equals(Expression.zero))
			return Expression.zero;
		else if (dividend.equals(Expression.one))
			return divisor.reciprocate();
		else
			return new Product(dividend, divisor.reciprocate());
	}

	public static Expression divide(Variable dividend, Variable divisor) {
		if (dividend.equals(divisor))
			return Expression.one;
		else
			return new Product(dividend, divisor.reciprocate());
	}

	public static Expression divide(Product dividend, Variable divisor) {
		return product_divide(dividend, divisor);
	}

	// TODO
	public static Expression pow(Integer b, Integer e) {
		return Integer.of(ExponentiationUtils.pow(b.number(), e.number()));
	}

	// TODO can be optimized much further
	static boolean canAdde(Expression a1, Expression a2) {
		if (a1 instanceof Sum a1sum) {
			for (Expression e : a1sum.addends())
				if (canAdde(a2, e))
					return true;
			return false;
		} else if (a2 instanceof Sum a2sum) {
			for (Expression e : a2sum.addends())
				if (canAdde(a1, e))
					return true;
			return false;
		} else {
			return !(a1.add(a2) instanceof Sum);
		}

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
