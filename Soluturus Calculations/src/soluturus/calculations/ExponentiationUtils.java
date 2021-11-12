package soluturus.calculations;

import static java.math.BigInteger.ONE;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Defines numerous static utility methods for Exponentiation, N<sup>th</sup>
 * root, and Logarithmic operations.
 * <p>
 * All operations can be used with any BigDecimal in the applicable arguments.
 * They are able to perform operations that
 * <p>
 * For optimal performance, methods will call predefined, but limited, methods
 * if those will return a satisfactory result.
 * 
 * @author Miles K Bertrand
 *
 */
public final class ExponentiationUtils {

	private static final int PRIME_CERTAINTY = 1000;
	private static final BigInteger MAX_POW_INT = BigInteger.valueOf(999999999);

	private ExponentiationUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Calculates base<sup>exponent</sup>, where the exponent can be either whole or
	 * Fractional.
	 * <p>
	 * This method contains maximum optimization and is as flexible as possible for
	 * undesirable values, such as negative exponents and large numbers.
	 * 
	 * @param base     the base of the exponent
	 * @param exponent the power to which the base is raised
	 * @param mc       the context to be used in rounding values
	 * @return base<sup>exponent</sup>
	 * @throws ArithmeticException if the base is negative and the denominator of
	 *                             the fully simplified fractional form of the
	 *                             exponent is even.
	 * @throws ArithmeticException if the result of any operation is inexact but the
	 *                             rounding mode of context is unnecessary.
	 */
	public static final BigDecimal pow(BigDecimal base, BigDecimal exponent, final MathContext mc) {

		if (exponent.signum() == -1)
			return pow(BigDecimal.ONE.divide(base, mc), exponent, mc);
		if (base.signum() == 0)
			return BigDecimal.ZERO;
		else if (exponent.signum() == 0)
			return BigDecimal.ONE;

		final BigInteger numerator = exponent.scaleByPowerOfTen(exponent.scale()).toBigInteger();
		final BigInteger denominator = BigDecimal.valueOf(1, -exponent.scale()).toBigInteger();
		final BigInteger gcd = IntegerUtils.gcd(numerator, denominator);

		BigDecimal ans = base.abs().compareTo(BigDecimal.ONE) == 1
				? pow(root(base, denominator.divide(gcd), mc), numerator.divide(gcd), mc)
				: root(pow(base, numerator.divide(gcd), mc), denominator.divide(gcd), mc);
		return ans;
	}

	/**
	 * Calculates base<sup>exponent</sup>, where the exponent is whole.
	 * <p>
	 * Utilizes repeated squaring, the fastest known method of exponentiation.
	 * 
	 * @param base     the base of the exponent
	 * @param exponent the power to which the base is raised
	 * @param mc       the context to be used in rounding values
	 * @return base<sup>exponent</sup>
	 * @throws ArithmeticException if the result of any operation is inexact but the
	 *                             rounding mode of context is unnecessary.
	 */
	public static final BigDecimal pow(BigDecimal base, BigInteger exponent, final MathContext mc) {

		if (base.signum() == -1)
			return exponent.remainder(BigInteger.TWO).signum() == 0 ? pow(base.abs(), exponent, mc)
					: pow(base.abs(), exponent, mc).negate();
		else if (base.signum() == 0)
			return BigDecimal.ZERO;
		else if (base.equals(BigDecimal.ONE))
			return BigDecimal.ONE;
		else if (exponent.compareTo(MAX_POW_INT) != 1)
			return base.pow(exponent.intValue(), mc).round(mc);

		BigDecimal result = BigDecimal.ONE;

		while (exponent.signum() == 1) {
			if (exponent.testBit(0))
				result = result.multiply(base, mc);
			base = base.multiply(base, mc);
			exponent = exponent.shiftRight(1);
		}
		return result.round(mc);
	}

	/**
	 * Calculates radicand<sup><small>1</small>/<small>degree</small></sup>,
	 * rounding as specified by context.
	 * <p>
	 * To increase performance, splits the degree into its prime factors and finds
	 * the root for each factor.
	 * 
	 * @param radicand
	 * @param degree
	 * @param mc
	 * @return the base<sup>th</sup> root of radicand
	 * @throws ArithmeticException if the base is negative and the denominator of
	 *                             the fully simplified fractional form of the
	 *                             exponent is even.
	 * @throws ArithmeticException if the result of any operation is inexact but the
	 *                             rounding mode of context is unnecessary.
	 */
	public static final BigDecimal root(BigDecimal radicand, final BigInteger degree, final MathContext mc) {

		if (degree.signum() == 0 && radicand.signum() == -1 && degree.remainder(BigInteger.TWO).signum() == 0)
			throw new ArithmeticException();

		else if (radicand.signum() == -1)
			return root0(radicand.negate(), degree, mc).negate();
		else if (radicand.signum() == 0)
			return BigDecimal.ZERO;
		else if (radicand.equals(BigDecimal.ONE))
			return BigDecimal.ONE;
		else if (degree.equals(BigInteger.TWO))
			return radicand.sqrt(mc);
		else if (degree.isProbablePrime(PRIME_CERTAINTY))
			return root0(radicand, degree, mc);

		for (BigInteger b : IntegerUtils.factor(degree))
			radicand = b.equals(BigInteger.TWO) ? radicand.sqrt(mc) : root0(radicand, b, mc);

		return radicand;
	}

	/**
	 * Calculates the base<sup>th</sup> root of radicand using Newton's method, the
	 * fastest nth root algorithm.
	 * 
	 * @param radicand
	 * @param degree
	 * @param mc
	 * @return base<sup>th</sup> root of radicand
	 */
	private static final BigDecimal root0(final BigDecimal radicand, final BigInteger degree, final MathContext mc) {

		final BigDecimal bdpow = new BigDecimal(degree);
		BigDecimal x_prev = radicand;
		BigDecimal x = radicand.divide(bdpow, mc);

		while (!x.equals(x_prev)) {
			x_prev = x;
			x = bdpow.subtract(BigDecimal.ONE).multiply(x)
					.add(radicand.divide(ExponentiationUtils.pow(x, degree.subtract(ONE), MathContext.UNLIMITED), mc))
					.divide(bdpow, mc).round(mc);
		}

		return x.round(mc);
	}

	public static final boolean isPowerOf(BigDecimal number, BigDecimal base, final MathContext mc) {

		number = number.round(mc);
		base = base.round(mc);

		Objects.requireNonNull(number);

		if (number.equals(BigDecimal.ONE))
			return true;

		BigDecimal n = BigDecimal.ONE;

		if (base.abs().compareTo(BigDecimal.ONE) == 1)
			while (n.compareTo(number) == -1)
				n = n.multiply(base, mc);
		else
			while (n.abs().compareTo(number.abs()) == 1)
				n = n.multiply(base, mc);

		return n.compareTo(number) == 0;
	}

	/**
	 * Calculates the log<sub>base</sub><small>number</small> to precision mc.
	 * <p>
	 * Since calculating the logarithm is a very expensive operation, care should be
	 * taken within algorithms to avoid it at all costs.
	 * 
	 * @param number
	 * @param base
	 * @param mc
	 * @return
	 */
	public static BigDecimal log(final BigDecimal number, final BigDecimal base, MathContext mc) {

		BigDecimal result = BigDecimal.ZERO;

		final MathContext contextdown = new MathContext(mc.getPrecision(), RoundingMode.DOWN);

		BigDecimal numbercopy = number;

		while (numbercopy.compareTo(base) > -1) {
			result = result.add(BigDecimal.ONE);
			numbercopy = numbercopy.divide(base, contextdown);
		}

		if (isPowerOf(number, base, mc))
			return result;

		final MathContext mc2 = new MathContext(mc.getPrecision() + 6, mc.getRoundingMode());

		for (int i = 1; i <= mc.getPrecision(); i++)
			while (pow(base, result.add(BigDecimal.valueOf(1, i)), mc2).compareTo(number) == -1)
				result = result.add(BigDecimal.valueOf(1, i));

		return result.round(mc);
	}

	public static final BigInteger pow(BigInteger base, BigInteger exponent) {

		BigInteger result = ONE;

		while (exponent.signum() == 1) {
			if (exponent.testBit(0))
				result = result.multiply(base);

			base = base.pow(2);
			exponent = exponent.shiftRight(1);
		}

		return result;
	}

	public static final boolean isPowerOf(final BigInteger number, final BigInteger base) {
		return isPowerOf(new BigDecimal(number), new BigDecimal(base), MathContext.UNLIMITED);
	}
}
