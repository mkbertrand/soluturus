package soluturus.base.internal.trigonometric;

import soluturus.base.Trigonometric;
import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Variable;

public record Sine(Expression angle) implements Expression {

	@Override
	public Sine clone() {
		return new Sine(angle);
	}

	@Override
	public Expression add(Expression addend) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression multiply(Expression multiplicand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression divide(Expression dividend) {
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
		return new Sine(angle.add(pi));
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
		return angle.derive(v).multiply(Trigonometric.cos(angle));
	}

	@Override
	public Expression substitute(Variable v, Expression replacement) {
		return new Sine(angle.substitute(v, replacement));
	}

	@Override
	public Expression[] factor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final boolean isKnown() {
		return angle.isKnown();
	}

	@Override
	public boolean isMonomial() {
		return false;
	}

	@Override
	public boolean isPolynomial() {
		return false;
	}
}
