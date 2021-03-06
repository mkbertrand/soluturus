package soluturus.base;

import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Integer;

public final class Trigonometric {
	
	private Trigonometric() {
		throw new UnsupportedOperationException();
	}
	
	public static Expression sin(Expression e) {
		return e.sin();
	}

	public static Expression cos(Expression e) {
		return sin(e.add(Expression.pi.divide(new Integer(2))));
	}

	public static Expression tan(Expression e) {
		return sin(e).divide(cos(e));
	}

	public static Expression sec(Expression e) {
		return cos(e).reciprocate();
	}

	public static Expression csc(Expression e) {
		return sin(e).reciprocate();
	}

	public static Expression cot(Expression e) {
		return cos(e).divide(sin(e));
	}
}
