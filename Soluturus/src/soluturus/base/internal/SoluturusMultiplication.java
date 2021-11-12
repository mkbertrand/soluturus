package soluturus.base.internal;

import soluturus.base.algebraic.Integer;
import soluturus.base.algebraic.Power;
import soluturus.base.algebraic.Product;
import soluturus.base.algebraic.Sum;
import soluturus.base.algebraic.Variable;
import soluturus.base.exposed.Expression;

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
		else if (m2 instanceof Power mu2)
			return multiply(m1, mu2);
		return null;
	}

	// m2 is guaranteed not to be an Integer or Variable
	public static Expression multiply(Sum m1, Expression m2) {
		if (m2 instanceof Sum mu2)
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

				System.out.println("	" + factors[i] + " * " + m2 + " = " + prod);
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
		result[factors.length] = m2;
		return new Product(result);
	}

	public static Integer multiply(Integer m1, Integer m2) {
		return new Integer(m1.number().multiply(m2.number()));
	}

	public static Expression multiply(Integer m1, Variable m2) {
		if (m1.equals(Expression.zero))
			return Expression.zero;
		return new Product(m1, m2);
	}

	public static Expression multiply(Integer m1, Sum m2) {
		return sum_multiply(m2, m1);
	}

	public static Expression multiply(Integer m1, Power m2) {
		if (m2.exponent().equals(Expression.negative_one))
			return m1.divide(m2.base());
		else if (m2.base().equals(m1)) {
			// TODO
			return null;
		} else
			return new Product(m1, m2);
	}

	public static Expression multiply(Variable m1, Variable m2) {
		if (m1.equals(m2))
			return new Power(m1, Expression.two);
		else
			return new Product(m1, m2);
	}

	public static Expression multiply(Variable m1, Sum m2) {
		return sum_multiply(m2, m1);
	}

	public static Expression multiply(Variable m1, Power m2) {
		System.out.println("here :)");
		return null;
	}

	public static Expression multiply(Sum m1, Sum m2) {
		System.out.println("hi!");
		Expression[] addends1 = m1.addends();
		Expression[] addends2 = m2.addends();

		Expression product = Expression.zero;
		for (int i = 0; i < addends1.length; i++)
			for (int j = 0; j < addends2.length; j++) {
				System.out.println(product + " + " + addends1[i].multiply(addends2[j]));
				product = product.add(addends1[i].multiply(addends2[j]));
			}
		return product;
	}

	public static Expression multiply(Product m1, Product m2) {
		Expression product = m1;
		for (Expression e : m2.factors())
			product = product.multiply(e);
		return product;
	}

	public static Expression multiply(Product m1, Power m2) {

		System.out.println(m1 + " * " + m2 + " = " + product_multiply(m1, m2));
		// TODO
		return product_multiply(m1, m2);
	}

	public static Expression multiply(Power m1, Power m2) {
		// TODO
		if (m1.exponent().equals(m2.exponent()) && SoluturusMath.canMultiply(m1.base(), m2.base()))
			return m1.base().multiply(m2.base()).pow(m1.exponent());
		return new Product(m1, m2);
	}
}
