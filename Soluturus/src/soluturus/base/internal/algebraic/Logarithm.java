package soluturus.base.internal.algebraic;

import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Variable;

public record Logarithm(Expression base, Expression number) implements Expression {

	@Override
	public Expression clone() {
		return new Logarithm(base, number);
	}

	@Override
	public Expression add(Expression addend) {
		if (addend.equals(Expression.zero))
			return this;
		else if (addend instanceof Logarithm al2 && al2.base.equals(base))
			return Expression.log(base, number.multiply(al2.number));
		// TODO
		else
			return new Sum(this, addend);
	}

	@Override
	public Expression multiply(Expression multiplicand) {
		if (multiplicand.equals(Expression.one))
			return this;
		else if (multiplicand instanceof Logarithm ml2 && number.equals(ml2.base))
			return Expression.log(base, ml2.number);
		// TODO
		else
			return Expression.log(base, number.pow(multiplicand));
	}

	@Override
	public Expression divide(Expression divisor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression pow(Expression exponent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression log(Expression base) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression negate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression reciprocate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression sin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression derive(Variable v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression substitute(Variable v, Expression replacement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isKnown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMonomial() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPolynomial() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		if (base.equals(Expression.e))
			return "ln(" + number + ")";
		else
			return "log(" + base + "," + number + ")";
	}
}
