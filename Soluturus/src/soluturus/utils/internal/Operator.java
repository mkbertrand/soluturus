package soluturus.utils.internal;

import java.util.function.BinaryOperator;

import soluturus.base.expressions.Expression;

/**
 * In an expression, Operators are used to refer to mathematical operations to
 * be performed on Numeric operands.
 * 
 * @author Miles K. Bertrand
 *
 */
public enum Operator implements Symbol {

	/**
	 * Signifies the addition of an augend and an addend.
	 * 
	 * @see lambda.core.numbers.Number#add(Number)
	 */
	ADD('+', (augend, addend) -> augend.add(addend)),

	/**
	 * Signifies the subtraction of a subtrahend from a minuend.
	 * 
	 * @see lambda.core.numbers.Number#subtract(Number)
	 */
	SUBTRACT('-', (minuend, subtrahend) -> minuend.subtract(subtrahend)),

	/**
	 * Signifies the multiplication of a multiplier and a multiplicand.
	 * 
	 * @see lambda.core.numbers.Number#multiply(Number)
	 */
	MULTIPLY('*', (multiplier, multiplicand) -> multiplier.multiply(multiplicand)),

	/**
	 * Signifies the division of a dividend over a divisor.
	 * 
	 * @see lambda.core.numbers.Number#divide(Number)
	 */
	DIVIDE('/', (dividend, divisor) -> dividend.divide(divisor)),

	/**
	 * Signifies the exponentiation of a base to an exponent.
	 * 
	 * @see lambda.core.numbers.Number#pow(Number)
	 */
	POW('^', (base, exponent) -> base.pow(exponent)),

	/**
	 * Signifies the calculating of the [degree]<sup>th</sup> root of a radicand.
	 * 
	 * @see lambda.core.numbers.Number#root(Number)
	 */
	ROOT('@', (radicand, degree) -> radicand.root(degree)),

	/**
	 * Signifies the calculation of a logarithm of a number with a specified base.
	 * <p>
	 * Since the logarithm operation is generally written in plaintext as
	 * log(number, base), which is difficult for the parser and simplifier to
	 * understand, log([number], [base]) is replaced with [number]Â¡[base].
	 * 
	 * @see lambda.core.numbers.Number#log(Number)
	 */
	LOG('#', (number, base) -> number.log(base));

	private final char symbol;

	private final BinaryOperator<Expression> action;

	Operator(char symbol, BinaryOperator<Expression> action) {
		this.symbol = symbol;
		this.action = action;
	}

	@Override
	public String toString() {
		return Character.toString(symbol);
	}

	public Expression act(final Expression a, final Expression b) {
		return action.apply(a, b);
	}
}
