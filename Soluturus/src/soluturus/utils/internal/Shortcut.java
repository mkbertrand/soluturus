package soluturus.utils.internal;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import soluturus.base.expressions.Expression;

public final class Shortcut {

	private final Function<Expression[], Expression> action;
	private final int minargs;
	private final int maxargs;

	public Shortcut(final UnaryOperator<Expression> action) {
		this(n -> action.apply(n[0]), 1, 1);
	}

	public Shortcut(final Function<Expression[], Expression> action) {
		this(action, 0, -1);
	}

	public Shortcut(final Function<Expression[], Expression> action, final int minargs, final int maxargs) {
		this.action = action;
		this.minargs = minargs;
		this.maxargs = maxargs;
	}

	public final Expression apply(Expression... args) {
		if (args.length < minargs || maxargs != -1 && args.length > maxargs)
			throw new IllegalArgumentException();
		return action.apply(args);
	}
}
