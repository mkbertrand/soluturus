package soluturus.base.exposed;

import soluturus.base.algebraic.Integer;
import soluturus.base.algebraic.Variable;

public interface Expression {

	public static final Variable pi = new Variable("π");
	public static final Integer negative_one = Integer.of(-1);
	public static final Integer zero = Integer.of(0);
	public static final Integer one = Integer.of(1);
	public static final Integer two = Integer.of(2);

	public Expression add(Expression addend);

	public default Expression subtract(Expression subtrahend) {
		return add(subtrahend.negate());
	}

	public Expression multiply(Expression multiplicand);

	public default Expression divide(Expression dividend) {
		return null;
	}

	public abstract Expression negate();

	public abstract Expression reciprocate();

	public default Expression pow(Expression exponent) {
		return null;
	}

	public default Expression add(Expression... addends) {

		Expression sum = addends[0];

		for (int i = 1; i < addends.length; i++)
			sum = sum.add(addends[i]);

		return sum;
	}

	public default Expression multiply(Expression... multiplicands) {

		Expression product = multiplicands[0];

		for (int i = 1; i < multiplicands.length; i++)
			product = product.multiply(multiplicands[i]);

		return product;
	}

	public boolean isKnown();

	public default Expression sin() {
		// TODO
		return null;
	}
}
