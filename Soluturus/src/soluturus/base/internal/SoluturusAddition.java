package soluturus.base.internal;

import java.math.BigInteger;
import java.util.ArrayList;

import soluturus.base.algebraic.Integer;
import soluturus.base.algebraic.Power;
import soluturus.base.algebraic.Product;
import soluturus.base.algebraic.Sum;
import soluturus.base.algebraic.Variable;
import soluturus.base.exposed.Expression;

public final class SoluturusAddition {

	private SoluturusAddition() {
		throw new UnsupportedOperationException();
	}
	
	public static Expression add(Integer a1, Expression a2) {
		if (a2 instanceof Integer ad2)
			return add(a1, ad2);
		else if (a2 instanceof Variable ad2)
			return add(a1, ad2);
		else if (a2 instanceof Sum ad2)
			return add(a1, ad2);
		else if (a2 instanceof Product ad2)
			return add(a1, ad2);
		return null;
	}

	// a2 is guaranteed not to be an Integer
	public static Expression add(Variable a1, Expression a2) {
		if (a2 instanceof Variable ad2)
			return add(a1, ad2);
		else if (a2 instanceof Sum ad2)
			return add(a1, ad2);
		else if (a2 instanceof Product ad2)
			return add(a1, ad2);
		return null;
	}

	// a2 is guaranteed not to be an Integer or Variable
	public static Expression add(Sum a1, Expression a2) {
		if (a2 instanceof Sum ad2)
			return add(a1, ad2);
		else if (a2 instanceof Product ad2)
			return add(a1, ad2);
		return null;
	}

	// a2 is guaranteed not to be an Integer, Variable, or Sum
	public static Expression add(Product a1, Expression a2) {
		if (a2 instanceof Product ad2)
			return add(a1, ad2);
		return null;
	}
	
	private static Expression sum_add(Sum a1, Expression a2) {
		Expression[] a1addends = a1.addends();
		for (int i = 0; i < a1.length(); i++)
			if (SoluturusMath.canAdd(a1addends[i], a2))
				if (a1addends[i].equals(a2.negate()))
					if (a1addends.length == 2)
						return i == 0 ? a1addends[1] : a1addends[2];
					else {
						Expression[] result = new Expression[a1addends.length - 1];
						System.arraycopy(a1addends, 0, result, 0, i);
						System.arraycopy(a1addends, i + 1, result, i, a1addends.length - i - 1);
						return new Sum(result);
					}
				else {
					a1addends[i] = a2.add(a1addends[i]);
					return new Sum(a1addends);
				}

		Expression[] result = new Expression[a1addends.length + 1];
		System.arraycopy(a1addends, 0, result, 0, a1addends.length);
		result[a1addends.length] = a2;
		return new Sum(result);
	}

	private static Expression product_add(Product a1, Expression a2) {
		// TODO early & dirty prototype
		Expression[] a1comps = a1.factors();

		BigInteger a1l = BigInteger.TWO.pow(a1comps.length);

		for (BigInteger i = BigInteger.ZERO; i.compareTo(a1l) < 0; i = i.add(BigInteger.ONE)) {
			ArrayList<Expression> bcomps = new ArrayList<>();
			ArrayList<Expression> ccomps = new ArrayList<>();
			for (int j = 0; j < a1comps.length; j++)
				if (i.testBit(j))
					bcomps.add(a1comps[j]);
				else
					ccomps.add(a1comps[j]);
			Product b = new Product(ccomps.toArray(new Expression[bcomps.size()]));
			Product c = new Product(ccomps.toArray(new Expression[ccomps.size()]));
			if (SoluturusMath.canAdd(a2.multiply(c.reciprocate()), b))
				return a2.multiply(c.reciprocate()).add(b).divide(c);
		}

		return new Sum(a1, a2);
	}


	public static Integer add(Integer a1, Integer a2) {
		return Integer.of(a1.number().add(a2.number()));
	}

	public static Sum add(Integer a1, Variable a2) {
		return new Sum(a1, a2);
	}

	public static Expression add(Integer a1, Sum a2) {
		return sum_add(a2, a1);
	}

	public static Expression add(Integer a1, Product a2) {
		// TODO check first two if statements and possibly implement generalized formula
		Expression[] a2comp = a2.factors();

		if (a2comp[0]instanceof Power a2comp0 && a2comp0.exponent().equals(Expression.negative_one)
				&& a2comp0.base() instanceof Integer && a2comp[1] instanceof Integer)
			return a2comp0.base().multiply(a1).add(a2comp[1]).divide(a2comp[0]);
		else if (a2comp[1]instanceof Power a2comp1 && a2comp1.exponent().equals(Expression.negative_one)
				&& a2comp1.base() instanceof Integer && a2comp[0] instanceof Integer)
			return a2comp1.base().multiply(a1).add(a2comp[0]).divide(a2comp[1]);
		else
			return new Sum(a1, a2);
	}

	public static Expression add(Integer a1, Power a2) {
		if (a2.base() instanceof Integer && a2.exponent().equals(Expression.negative_one))
			// a+b^-1 = (a*b+1) / b, and a*b+1 cannot be divisible by b when b != 1
			return new Product(a1.multiply(a2.base()).add(Expression.one), a2);
		else
			// TODO check whether any other situations could result in non-sum
			return new Sum(a1, a2);
	}

	public static Expression add(Variable a1, Variable a2) {
		return a1.equals(a2) ? new Product(Expression.two, a1) : new Sum(a1, a2);
	}

	public static Expression add(Variable a1, Sum a2) {
		return sum_add(a2, a1);
	}

	public static Expression add(Variable a1, Power a2) {
		return new Sum(a1, a2);
	}

	public static Expression add(Sum a1, Sum a2) {
		return a1.add(a2.addends());
	}

	public static Expression add(Sum a1, Product a2) {
		return sum_add(a1, a2);
	}

	public static Expression add(Product a1, Product a2) {
		return product_add(a1, a2);
	}

}