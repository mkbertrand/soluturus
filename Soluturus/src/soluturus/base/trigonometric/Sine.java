package soluturus.base.trigonometric;

import soluturus.base.exposed.Expression;

public final record Sine(Expression angle) implements Trigonometric {

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
	public Expression negate() {
		return new Sine(angle.add(pi));
	}

	@Override
	public Expression reciprocate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final boolean isKnown() {
		return angle.isKnown();
	}
}
