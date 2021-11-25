package soluturus.base.internal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Integer;
import soluturus.base.expressions.Variable;
import soluturus.base.internal.algebraic.Power;
import soluturus.base.internal.algebraic.Product;
import soluturus.base.internal.algebraic.Sum;

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
		else if (a2 instanceof Power ad2)
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
		else if (a2 instanceof Power ad2)
			return add(a1, ad2);
		return null;
	}

	// a2 is guaranteed not to be an Integer or Variable
	public static Expression add(Sum a1, Expression a2) {
		if (a2 instanceof Sum ad2)
			return add(a1, ad2);
		else if (a2 instanceof Product ad2)
			return add(a1, ad2);
		else if (a2 instanceof Power ad2)
			return add(a1, ad2);
		return null;
	}

	// a2 is guaranteed not to be an Integer, Variable, or Sum
	public static Expression add(Product a1, Expression a2) {
		if (a2 instanceof Product ad2)
			return add(a1, ad2);
		else if (a2 instanceof Power ad2)
			return add(a1, ad2);
		return null;
	}

	// a2 is guaranteed not to be an Integer, Variable, Sum, or Product
	public static Expression add(Power a1, Expression a2) {
		if (a2 instanceof Power ad2)
			return add(a1, ad2);
		return null;
	}

	private static Expression sum_add(Sum a1, Expression a2) {
		Expression[] a1addends = a1.addends();
		for (int i = 0; i < a1.length(); i++)
			if (SoluturusCanAdd.canAdd(a1addends[i], a2))
				if (a1addends[i].equals(a2.negate()))
					if (a1addends.length == 2)
						return i == 0 ? a1addends[1] : a1addends[0];
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

			if (bcomps.size() == 0 || ccomps.size() == 0)
				continue;

			Expression b = bcomps.size() == 1 ? bcomps.get(0) : new Product(bcomps);
			Expression c = ccomps.size() == 1 ? ccomps.get(0) : new Product(ccomps);

			if (SoluturusCanAdd.canAdd(a2.multiply(c.reciprocate()), b))
				return a2.multiply(c.reciprocate()).add(b).multiply(c);
		}

		return new Sum(a1, a2);
	}

	/*
	 * Addition methods for Integers
	 */

	public static Expression add(Integer a1, Integer a2) {
		return Integer.of(a1.number().add(a2.number()));
	}

	public static Expression add(Integer a1, Variable a2) {
		if (a1.equals(Expression.zero))
			return a2;
		else
			return new Sum(a1, a2);
	}

	public static Expression add(Integer a1, Sum a2) {
		if (a1.equals(Expression.zero))
			return a2;
		else
			return sum_add(a2, a1);
	}

	public static Expression add(Integer a1, Product a2) {
		// TODO
		Expression[] a2comp = a2.factors();

		if (a1.equals(Expression.zero))
			return a2;
		else if (a2.isFraction())
			if (a2comp[0]instanceof Integer a20)
				return ((Power) a2comp[1]).base().multiply(a1).add(a20).divide(((Power) a2comp[1]).base());
			else
				return ((Power) a2comp[0]).base().multiply(a1).add(a2comp[1]).divide(((Power) a2comp[0]).base());
		else
			return new Sum(a1, a2);
	}

	public static Expression add(Integer a1, Power a2) {
		if (a1.equals(Expression.zero))
			return a2;
		else if (a2.isFraction())
			// a+b^-1 = (a*b+1) / b, and a*b+1 cannot be divisible by b when b != 1
			return new Product(a1.multiply(a2.base()).add(Expression.one), a2);
		else
			// TODO check whether any other situations could result in non-sum
			return new Sum(a1, a2);
	}

	/*
	 * Addition methods for Variables
	 */

	public static Expression add(Variable a1, Variable a2) {
		return a1.equals(a2) ? new Product(Expression.two, a1) : new Sum(a1, a2);
	}

	public static Expression add(Variable a1, Sum a2) {
		return sum_add(a2, a1);
	}

	public static Expression add(Variable a1, Product a2) {
		return product_add(a2, a1);
	}

	public static Expression add(Variable a1, Power a2) {
		return new Sum(a1, a2);
	}

	/*
	 * Addition methods for Sums
	 */

	public static Expression add(Sum a1, Sum a2) {
		return a1.add(a2.addends());
	}

	public static Expression add(Sum a1, Product a2) {
		return sum_add(a1, a2);
	}

	public static Expression add(Sum a1, Power a2) {
		return sum_add(a1, a2);
	}

	/*
	 * Addition methods for Products
	 */

	public static Expression add(Product a1, Product a2) {

		// TODO will need future factoring support

		ArrayList<Expression> factors1 = new ArrayList<>(Arrays.asList(a1.factors()));
		ArrayList<Expression> factors2 = new ArrayList<>(Arrays.asList(a2.factors()));

		ArrayList<Expression> commonFactors = new ArrayList<>();

		for (int i = 0; i < factors1.size(); i++)
			if (factors2.contains(factors1.get(i))) {
				factors2.remove(factors1.get(i));
				commonFactors.add(factors1.get(i));
				factors1.remove(i--);
			}

		// a*c + b*c = c * (a+b)

		Expression a = switch (factors1.size()) {
		case 0 -> Integer.of(1);
		case 1 -> factors1.get(0);
		default -> new Product(factors1);
		};

		Expression b = switch (factors2.size()) {
		case 0 -> Integer.of(1);
		case 1 -> factors2.get(0);
		default -> new Product(factors2);
		};

		Expression c = switch (commonFactors.size()) {
		case 0 -> Integer.of(1);
		case 1 -> commonFactors.get(0);
		default -> new Product(commonFactors);
		};

		if (SoluturusCanAdd.canAdd(a, b))
			return c.multiply(a.add(b));
		else
			return product_add(a1, a2);
	}

	public static Expression add(Product a1, Power a2) {
		return product_add(a1, a2);
	}

	/*
	 * Addition methods for Powers
	 */

	public static Expression add(Power a1, Power a2) {
		if (a1.isFraction() && a2.isFraction())
			return a1.base().add(a2.base()).divide(a1.base().multiply(a2.base()));
		// TODO
		return new Sum(a1, a2);
	}
}
