package soluturus.base.internal.trigonometric;

import soluturus.base.expressions.Expression;

public final record Sine(Expression angle) implements Expression {

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
	public Expression sin() {
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
	public Expression[] factor() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public final boolean isKnown() {
		return angle.isKnown();
	}
}
