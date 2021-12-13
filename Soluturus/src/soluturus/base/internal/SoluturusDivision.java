package soluturus.base.internal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import soluturus.base.exceptions.ZeroDivisionException;
import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Integer;
import soluturus.base.expressions.Variable;
import soluturus.base.internal.algebraic.Power;
import soluturus.base.internal.algebraic.Product;
import soluturus.base.internal.algebraic.Sum;
import soluturus.calculations.IntegerUtils;

public final class SoluturusDivision {

	private SoluturusDivision() {
		throw new UnsupportedOperationException();
	}

	public static Expression divide(Integer dividend, Expression divisor) {
		if (divisor instanceof Integer castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Variable castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Sum castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Product castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Power castdivisor)
			return divide(dividend, castdivisor);
		return null;
	}

	public static Expression divide(Variable dividend, Expression divisor) {
		if (divisor instanceof Integer castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Variable castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Sum castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Product castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Power castdivisor)
			return divide(dividend, castdivisor);
		return null;
	}

	public static Expression divide(Sum dividend, Expression divisor) {
		if (divisor instanceof Integer castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Variable castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Sum castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Product castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Power castdivisor)
			return divide(dividend, castdivisor);
		return null;
	}

	public static Expression divide(Product dividend, Expression divisor) {
		if (divisor instanceof Integer castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Variable castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Sum castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Product castdivisor)
			return divide(dividend, castdivisor);
		else if (divisor instanceof Power castdivisor)
			return divide(dividend, castdivisor);
		return null;
	}

	private static Expression product_dividend(Product dividend, Expression divisor) {

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

	private static Expression product_divisor(Expression dividend, Product divisor) {

		Expression[] factors = divisor.factors();

		for (int i = 0; i < factors.length; i++)
			if (SoluturusMath.canDivide(dividend, factors[i])) {
				Expression prod = dividend.divide(factors[i]);

				if (factors.length == 2)
					return prod.divide(factors[i == 0 ? 1 : 0]);
				else {
					Expression[] result = new Expression[factors.length - 1];
					System.arraycopy(factors, 0, result, 0, i);
					System.arraycopy(factors, i + 1, result, i, factors.length - i - 1);
					return prod.divide(new Product(result));
				}
			}

		Expression rec = divisor.reciprocate();

		if (rec instanceof Product recp) {
			factors = recp.factors();
			Expression[] result = new Expression[factors.length + 1];
			System.arraycopy(factors, 0, result, 0, factors.length);
			result[factors.length] = dividend;
			return new Product(result);
		} else
			return new Product(dividend, rec);
	}

	public static Expression divide(Integer dividend, Integer divisor) {

		if (divisor.equals(Expression.zero))
			throw new ZeroDivisionException();
		else if (dividend.equals(Expression.zero))
			return Expression.zero;

		boolean isNegative = dividend.number().signum() < 0 ^ divisor.number().signum() < 0;

		dividend = dividend.abs();
		divisor = divisor.abs();

		BigInteger gcd = IntegerUtils.gcd(dividend.number(), divisor.number());

		dividend = new Integer(dividend.number().divide(gcd));
		divisor = new Integer(divisor.number().divide(gcd));

		if (divisor.equals(Expression.one))
			return isNegative ? dividend.negate() : dividend;

		if (dividend.equals(Expression.one))
			return (isNegative ? divisor.negate() : divisor).reciprocate();
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

	public static Expression divide(Integer dividend, Product divisor) {
		return product_divisor(dividend, divisor);
	}

	public static Expression divide(Variable dividend, Integer divisor) {
		if (divisor.equals(Expression.zero))
			throw new ZeroDivisionException();
		else if (divisor.equals(Expression.one))
			return dividend;
		else if (divisor.equals(Expression.negative_one))
			return dividend.negate();
		else
			return new Product(dividend, divisor.reciprocate());
	}

	public static Expression divide(Variable dividend, Variable divisor) {
		if (dividend.equals(divisor))
			return Expression.one;
		else
			return new Product(dividend, divisor.reciprocate());
	}

	public static Expression divide(Variable dividend, Product divisor) {
		return product_divisor(dividend, divisor);
	}

	public static Expression divide(Sum dividend, Integer divisor) {
		Expression quotient = Expression.zero;
		for (Expression e : dividend)
			quotient = quotient.add(e.divide(divisor));
		return quotient;
	}

	public static Expression divide(Sum dividend, Variable divisor) {
		Expression quotient = Expression.zero;
		for (Expression e : dividend)
			quotient = quotient.add(e.divide(divisor));
		return quotient;
	}

	public static Expression divide(Sum dividend, Sum divisor) {

		ArrayList<Expression> divnFactors = new ArrayList<>(Arrays.asList(dividend.factor()));
		ArrayList<Expression> divrFactors = new ArrayList<>(Arrays.asList(divisor.factor()));

		divrFactors.removeIf(divnFactors::remove);

		Expression newdividend = Expression.product(divnFactors.toArray(new Expression[divnFactors.size()]));
		Expression newdivisor = Expression.product(divrFactors.toArray(new Expression[divrFactors.size()]));

		if (dividend.equals(newdividend))
			return new Product(dividend, divisor.reciprocate());
		else
			return newdividend.divide(newdivisor);
	}

	public static Expression divide(Sum dividend, Product divisor) {
		return product_divisor(dividend, divisor);
	}

	public static Expression divide(Product dividend, Integer divisor) {
		return product_dividend(dividend, divisor);
	}

	public static Expression divide(Product dividend, Variable divisor) {
		return product_dividend(dividend, divisor);
	}

	public static Expression divide(Product dividend, Power divisor) {
		return product_dividend(dividend, divisor);
	}
}
