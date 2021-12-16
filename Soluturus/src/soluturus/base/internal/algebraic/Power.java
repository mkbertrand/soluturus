package soluturus.base.internal.algebraic;

import soluturus.base.exceptions.ExpressionContainmentException;
import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Integer;
import soluturus.base.expressions.Variable;
import soluturus.base.internal.InternalAddition;
import soluturus.base.internal.InternalExponentiation;
import soluturus.base.internal.InternalMultiplication;

/**
 * Represents a base raised to a power wherein base<sup>exponent</sup> is the
 * fully simplified form. It can be operated on as a single unit and can
 * interact with any other Expression.
 * <p>
 * Powers may not contain Powers or Products and the base position and may not
 * have Sums in the exponent position.
 * 
 * @author Miles K. Bertrand
 * 
 */
public final record Power(Expression base, Expression exponent) implements Expression {

	public Power(Expression base, Expression exponent) {
		if (base instanceof Power || base instanceof Product || exponent instanceof Sum)
			throw new ExpressionContainmentException();
		this.base = base;
		this.exponent = exponent;
	}

	@Override
	public Power clone() {
		return new Power(base, exponent);
	}

	@Override
	public Expression add(Expression addend) {

		if (addend instanceof Integer a2)
			return InternalAddition.add(a2, this);
		else if (addend instanceof Variable a2)
			return InternalAddition.add(a2, this);
		else if (addend instanceof Sum a2)
			return InternalAddition.add(a2, this);
		else if (addend instanceof Product a2)
			return InternalAddition.add(a2, this);
		else
			return InternalAddition.add(this, addend);
	}

	@Override
	public Expression multiply(Expression multiplicand) {
		if (multiplicand instanceof Integer m2)
			return InternalMultiplication.multiply(m2, this);
		else if (multiplicand instanceof Variable m2)
			return InternalMultiplication.multiply(m2, this);
		else if (multiplicand instanceof Sum m2)
			return InternalMultiplication.multiply(m2, this);
		else if (multiplicand instanceof Product m2)
			return InternalMultiplication.multiply(m2, this);
		return InternalMultiplication.multiply(this, multiplicand);
	}

	@Override
	public Expression divide(Expression divisor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression pow(Expression exponent) {
		return InternalExponentiation.pow(this, exponent);
	}

	@Override
	public Expression log(Expression base) {
		// TODO
		if (base.equals(this.base))
			return exponent;
		else if (!(Expression.log(base, this.base) instanceof Logarithm))
			return Expression.log(base, this.base).multiply(exponent);
		else
			return new Logarithm(base, this);
	}

	@Override
	public Expression negate() {
		// TODO
		if (exponent.equals(negative_one))
			return base.negate().reciprocate();
		else
			return new Product(negative_one, this);
	}

	@Override
	public Expression reciprocate() {
		if (exponent.equals(negative_one))
			return base;
		else if (exponent instanceof Integer exponent0 && exponent0.number().signum() == -1)
			return new Power(base, exponent.negate());
		return base.pow(exponent.negate());
	}

	@Override
	public Expression sin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression derive(Variable v) {
		// TODO check
		if (exponent.derive(v).equals(zero))
			return exponent.multiply(base.pow(exponent.subtract(one)), base.derive(v));
		else if (base.derive(v).equals(zero))
			return multiply(Expression.ln(base), exponent.derive(v));
		else
			return multiply(exponent.derive(v).multiply(Expression.ln(base))
					.add(exponent.multiply(base, base.derive(v).reciprocate())));
	}

	@Override
	public Expression substitute(Variable v, Expression replacement) {
		return base.substitute(v, replacement).pow(exponent.substitute(v, replacement));
	}

	@Override
	public Expression[] factor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isKnown() {
		return base.isKnown() && exponent.isKnown();
	}

	@Override
	public boolean isMonomial() {
		return exponent instanceof Integer && (base instanceof Variable || base instanceof Integer);
	}

	@Override
	public boolean isPolynomial() {
		return false;
	}

	// Returns whether this Power represents a fraction with a numerator of one.
	@Override
	public boolean isFraction() {
		return base instanceof Integer && exponent.equals(negative_one);
	}

	@Override
	public String toString() {
		// TODO
		if (isFraction())
			return ((Integer) base).signum() < 0 ? "-1/" + base.negate() : "1/" + base;
		else
			return base + "^" + exponent;
	}
}
