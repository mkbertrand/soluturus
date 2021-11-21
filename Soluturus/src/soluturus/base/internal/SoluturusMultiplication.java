package soluturus.base.internal;

import java.math.BigInteger;
import java.util.ArrayList;

import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Integer;
import soluturus.base.expressions.Variable;
import soluturus.base.internal.algebraic.Power;
import soluturus.base.internal.algebraic.Product;
import soluturus.base.internal.algebraic.Sum;
import soluturus.calculations.IntegerUtils;

public final class SoluturusMultiplication {

	private SoluturusMultiplication() {
		throw new UnsupportedOperationException();
	}

	public static Expression multiply(Integer m1, Expression m2) {
		if (m2 instanceof Integer mu2)
			return multiply(m1, mu2);
		else if (m2 instanceof Variable mu2)
			return multiply(m1, mu2);
		else if (m2 instanceof Sum mu2)
			return multiply(m1, mu2);
		else if (m2 instanceof Product mu2)
			return multiply(m1, mu2);
		else if (m2 instanceof Power mu2)
			return multiply(m1, mu2);
		return null;
	}

	// m2 is guaranteed not to be an Integer
	public static Expression multiply(Variable m1, Expression m2) {
		if (m2 instanceof Variable mu2)
			return multiply(m1, mu2);
		else if (m2 instanceof Sum mu2)
			return multiply(m1, mu2);
		else if (m2 instanceof Product mu2)
			return multiply(m1, mu2);
		else if (m2 instanceof Power mu2)
			return multiply(m1, mu2);
		return null;
	}

	// m2 is guaranteed not to be an Integer or Variable
	public static Expression multiply(Sum m1, Expression m2) {
		if (m2 instanceof Sum mu2)
			return multiply(m1, mu2);
		else if (m2 instanceof Product mu2)
			return multiply(m1, mu2);
		else if (m2 instanceof Power mu2)
			return multiply(m1, mu2);
		return null;
	}

	// m2 is guaranteed not to be an Integer, Variable, or Sum
	public static Expression multiply(Product m1, Expression m2) {
		if (m2 instanceof Product mu2)
			return multiply(m1, mu2);
		else if (m2 instanceof Power mu2)
			return multiply(m1, mu2);
		return null;
	}

	// m2 is guaranteed not to be an Integer, Variable, Sum, or Product
	public static Expression multiply(Power m1, Expression m2) {
		if (m2 instanceof Power mu2)
			return multiply(m1, mu2);
		return null;
	}

	private static Expression sum_multiply(Sum m1, Expression m2) {

		Expression product = Expression.zero;
		for (Expression e : m1.addends())
			product = product.add(e.multiply(m2));
		return product;
	}

	private static Expression product_multiply(Product m1, Expression m2) {

		Expression[] factors = m1.factors();

		for (int i = 0; i < factors.length; i++)
			if (SoluturusMath.canMultiply(factors[i], m2)) {

				Expression prod = factors[i].multiply(m2);
				// TODO is this right?
				return SoluturusMath.productRemove(m1, i).multiply(prod);
			}

		Expression[] result = new Expression[factors.length + 1];
		System.arraycopy(factors, 0, result, 0, factors.length);
		result[factors.length] = m2;
		return new Product(result);
	}

	/*
	 * Multiplication methods for Integers
	 */

	public static Expression multiply(Integer m1, Integer m2) {
		return new Integer(m1.number().multiply(m2.number()));
	}

	public static Expression multiply(Integer m1, Variable m2) {
		if (m1.equals(Expression.zero))
			return Expression.zero;
		else if (m1.equals(Expression.one))
			return m2;
		else
			return new Product(m1, m2);
	}

	public static Expression multiply(Integer m1, Sum m2) {
		return sum_multiply(m2, m1);
	}

	public static Expression multiply(Integer m1, Product m2) {
		// TODO
		return product_multiply(m2, m1);
	}

	public static Expression multiply(Integer m1, Power m2) {
		if (m1.equals(Expression.zero))
			return Expression.zero;
		else if (m1.equals(Expression.one))
			return m2;
		else if (m2.exponent().equals(Expression.negative_one))
			return m1.divide(m2.base());
		else if (m2.base().equals(m1) && SoluturusCanAdd.canAdd(Expression.one, m2.exponent()))
			return m2.base().pow(m2.exponent().add(Expression.one));
		else if (m2.base()instanceof Integer base2) {

			BigInteger m1big = m1.number();

			BigInteger gcd = IntegerUtils.gcd(m1big, base2.number());

			// TODO fix this
			int pow = 1;
			BigInteger[] div = null;

			while ((div = m1big.divideAndRemainder(gcd))[1].equals(BigInteger.ZERO)) {
				m1big = div[0];
				pow++;
			}

			if (SoluturusCanAdd.canAdd(Integer.of(pow), m2.exponent())) {
				ArrayList<Expression> ret = new ArrayList<>();
				if (!m2.exponent().add(Integer.of(pow)).equals(Expression.one)) {
					ret.add(new Power(new Integer(gcd), m2.exponent().add(Integer.of(pow))));
					if (!m1big.equals(BigInteger.ONE))
						ret.add(Integer.of(m1big));
				} else
					ret.add(new Integer(gcd.add(m1big)));
				ret.add(new Power(new Integer(base2.number().divide(gcd)), m2.exponent()));

				return new Product(ret.toArray(new Expression[ret.size()]));
			} else
				return new Product(m1, m2);
		} else
			return new Product(m1, m2);
	}

	/*
	 * Multiplication methods for Variables
	 */

	public static Expression multiply(Variable m1, Variable m2) {
		if (m1.equals(m2))
			return new Power(m1, Expression.two);
		else
			return new Product(m1, m2);
	}

	public static Expression multiply(Variable m1, Sum m2) {
		return sum_multiply(m2, m1);
	}

	public static Expression multiply(Variable m1, Product m2) {
		// TODO
		return product_multiply(m2, m1);
	}

	public static Expression multiply(Variable m1, Power m2) {
		// TODO check this
		if (m2.exponent().equals(Expression.negative_one))
			return m1.divide(m2.base());
		else if (m2.base().equals(m1) && SoluturusCanAdd.canAdd(Expression.one, m2.exponent()))
			return m2.base().pow(m2.exponent().add(Expression.one));
		else
			return new Product(m1, m2);
	}

	/*
	 * Multiplication methods for Sums
	 */

	public static Expression multiply(Sum m1, Sum m2) {
		Expression[] addends1 = m1.addends();
		Expression[] addends2 = m2.addends();

		Expression product = Expression.zero;
		for (int i = 0; i < addends1.length; i++)
			for (int j = 0; j < addends2.length; j++)
				product = product.add(addends1[i].multiply(addends2[j]));

		return product;
	}

	public static Expression multiply(Sum m1, Product m2) {
		// TODO
		return sum_multiply(m1, m2);
	}

	public static Expression multiply(Sum m1, Power m2) {
		// TODO
		return sum_multiply(m1, m2);
	}

	/*
	 * Multiplication methods for Products
	 */

	public static Expression multiply(Product m1, Product m2) {
		Expression product = m1;
		for (Expression e : m2.factors())
			product = product.multiply(e);
		return product;
	}

	public static Expression multiply(Product m1, Power m2) {
		// TODO
		return product_multiply(m1, m2);
	}

	/*
	 * Multiplication methods for Powers
	 */

	public static Expression multiply(Power m1, Power m2) {
		// TODO
		if (m1.exponent().equals(m2.exponent()) && SoluturusMath.canMultiply(m1.base(), m2.base()))
			return m1.base().multiply(m2.base()).pow(m1.exponent());
		else if (m1.base().equals(m2.base()) && SoluturusCanAdd.canAdd(m1.exponent(), m2.exponent()))
			return m1.base().pow(m1.exponent().add(m2.exponent()));
		return new Product(m1, m2);
	}
}
