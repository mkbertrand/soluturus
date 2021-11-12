package soluturus.calculations;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Object that represents a mathematical Fraction in a non Number Framework
 * aware manner for internal usage in non symbolic contexts in which the end user does
 * not use the lambda.core module, which contains the main Fraction class.
 * <p>
 * Stores two {@link BigInteger BigIntegers}, which serve as the numerator and
 * denominator.
 * 
 * @author Miles K Bertrand
 *
 */
public class Fraction implements Comparable<Fraction> {

	public static final Fraction ONE = new Fraction(1);
	public static final Fraction FOUR = new Fraction(4);

	private final BigInteger numerator;
	private final BigInteger denominator;

	public Fraction(double number) {

		if (Double.isNaN(number))
			throw new IllegalArgumentException();

		int denom = 1;

		while (number % 1 != 0) {
			number *= 10;
			denom *= 10;
		}

		long gcd = PrimitiveMath.gcd((long) number, denom);
		numerator = BigInteger.valueOf((long) number / gcd);
		denominator = BigInteger.valueOf(denom / gcd);
	}

	public Fraction(final long number) {
		this(BigInteger.valueOf(number));
	}

	public Fraction(final long numerator, final long denominator) {
		this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator), true, true);
	}

	public Fraction(final BigDecimal number) {

		final BigInteger numerator = number.scaleByPowerOfTen(number.scale()).toBigInteger();
		final BigInteger denominator = BigDecimal.valueOf(1, -number.scale()).toBigInteger();
		final BigInteger gcd = IntegerUtils.gcd(numerator, denominator);
		this.numerator = numerator.divide(gcd);
		this.denominator = denominator.divide(gcd);
	}

	public Fraction(final BigInteger number) {
		this.numerator = number;
		this.denominator = BigInteger.ONE;
	}

	public Fraction(final BigInteger numerator, final BigInteger denominator) {
		this(numerator, denominator, true, true);
	}

	Fraction(BigInteger numerator, BigInteger denominator, final boolean checkSigns, final boolean removeGCD) {

		if (checkSigns && denominator.signum() == -1) {
			numerator = numerator.negate();
			denominator = denominator.abs();
		}
		if (removeGCD) {

			final BigInteger gcd = IntegerUtils.gcd(numerator, denominator);

			numerator = numerator.divide(gcd);
			denominator = denominator.divide(gcd);
		}

		this.numerator = numerator;
		this.denominator = denominator;
	}

	// Getters

	public final BigInteger getNumerator() {
		return numerator;
	}

	public final BigInteger getDenominator() {
		return denominator;
	}

	@Override
	public int compareTo(final Fraction o) {
		return numerator.multiply(o.denominator).compareTo(denominator.multiply(o.numerator));
	}

	@Override
	public boolean equals(final Object o) {

		if (!(o instanceof Fraction))
			return false;

		return numerator.equals(((Fraction) o).numerator) && denominator.equals(((Fraction) o).denominator);
	}

	@Override
	public String toString() {
		return numerator.toString() + (denominator.equals(BigInteger.ONE) ? "" : "/" + denominator.toString());
	}

	public Fraction abs() {
		return new Fraction(numerator.abs(), denominator, false, false);
	}

	public Fraction additiveInverse() {
		return new Fraction(numerator.negate(), denominator, false, false);
	}

	public Fraction reciprocal() {
		if (numerator.signum() == 0)
			throw new ArithmeticException();
		return new Fraction(denominator, numerator, true, false);
	}

	public Fraction add(final Fraction addend) {
		return new Fraction(numerator.multiply(addend.denominator).add(addend.numerator.multiply(denominator)),
				denominator.multiply(addend.denominator), false, true);
	}

	public Fraction subtract(final Fraction subtrahend) {
		return new Fraction(
				numerator.multiply(subtrahend.denominator).subtract(subtrahend.numerator.multiply(denominator)),
				denominator.multiply(subtrahend.denominator), false, true);
	}

	public Fraction multiply(Fraction multiplicand) {
		return new Fraction(numerator.multiply(multiplicand.numerator), denominator.multiply(multiplicand.denominator),
				false, true);
	}

	public Fraction divide(Fraction divisor) {
		return new Fraction(numerator.multiply(divisor.denominator), denominator.multiply(divisor.numerator), false,
				true);
	}
}
