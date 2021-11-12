package soluturus.calculations;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public final class IrrationalUtils {

	private IrrationalUtils() {
		throw new UnsupportedOperationException();
	}

	/*
	 * Static constants used in pi calculation.
	 */
	private static final BigDecimal n13591409 = BigDecimal.valueOf(13591409);
	private static final BigDecimal n545140134 = BigDecimal.valueOf(545140134);
	private static final BigDecimal nneg640320 = BigDecimal.valueOf(-640320);

	private static BigDecimal highest_acc_pi;
	private static MathContext highest_acc_context_pi;

	/**
	 * Calculates pi as specified by the {@link MathContext}.
	 * <p>
	 * The Chudnovsky algorithm, developed by the Chudnovsky brothers, is the
	 * fastest converging algorithm for pi. The following is the formula:
	 * <p>
	 * <img src=
	 * "https://wikimedia.org/api/rest_v1/media/math/render/svg/bf7303826e1e4fcba1e8f111880fa8b1b0c12c2b">
	 * <p>
	 * The most accurate calculated result is stored and rounded to the specified
	 * amount, reducing the actual calls to the algorithm itself, which, despite
	 * being the fastest converging algorithm, is still a costly operation.
	 * 
	 * @param mc
	 * @return pi calculated accurately to the specified number of digits and
	 *         rounded in the specified manner
	 */
	public static final BigDecimal pi(final MathContext mc) {

		if (highest_acc_pi != null && (mc.getPrecision() < highest_acc_context_pi.getPrecision()
				|| (mc.getRoundingMode() == mc.getRoundingMode()
						&& mc.getPrecision() == highest_acc_context_pi.getPrecision())))
			return highest_acc_pi.round(mc);

		if (highest_acc_context_pi != null && mc.getPrecision() == highest_acc_context_pi.getPrecision()) {
			MathContext oneHigher = new MathContext(mc.getPrecision() + 1, mc.getRoundingMode());
			highest_acc_pi = chudnovsky_algorithm(highest_acc_context_pi = oneHigher);
			return highest_acc_pi.round(mc);
		} else
			return highest_acc_pi = chudnovsky_algorithm(highest_acc_context_pi = mc);
	}

	/**
	 * Algorithm used to calculate pi to an arbitrary number of digits.
	 * <p>
	 * This method utilizes the Chudnovsky Algorithm, which is the fastest known
	 * algorithm to calculate pi.
	 * <p>
	 * <img src=
	 * "https://wikimedia.org/api/rest_v1/media/math/render/svg/bf7303826e1e4fcba1e8f111880fa8b1b0c12c2b">
	 * <p>
	 * Due to the high computing cost associated with calculating factorials, the
	 * algorithm takes advantage of the fact that the previous iteration did a large
	 * amount of the factorial calculations by using that number, stored as a
	 * variable outside the loop.
	 * <p>
	 * In order to determine whether the amount of iterations completed was
	 * sufficient, the loop will check before each iteration whether the previous
	 * sum, rounded to the specified amount of digits, was equal to the most
	 * recently calculated sum.
	 * 
	 * @param accuracy the number of digits to include
	 * @return pi to a specified amount of digits
	 */
	private static final BigDecimal chudnovsky_algorithm(final MathContext mc) {

		BigDecimal coeff = BigDecimal.valueOf(426880).multiply(BigDecimal.valueOf(10005).sqrt(mc));

		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal prevsum = BigDecimal.ONE.negate();
		BigDecimal k = BigDecimal.ZERO;

		// Instead of calculating the factorials every time, use the previous
		BigDecimal onefactorial = BigDecimal.ONE;
		BigDecimal threefactorial = BigDecimal.ONE;
		BigDecimal sixfactorial = BigDecimal.ONE;

		while (!prevsum.equals(sum)) {

			prevsum = sum;

			sum = sum.add(sixfactorial.multiply(n13591409.add(n545140134.multiply(k))).divide(
					threefactorial.multiply(onefactorial.pow(3)).multiply(nneg640320.pow(3 * k.intValue())), mc))
					.round(mc);

			final BigDecimal threek = k.multiply(BigDecimal.valueOf(3));
			final BigDecimal sixk = k.multiply(BigDecimal.valueOf(6));

			threefactorial = threefactorial.multiply(threek.add(BigDecimal.ONE))
					.multiply(threek.add(BigDecimal.valueOf(2))).multiply(threek.add(BigDecimal.valueOf(3)));

			sixfactorial = sixfactorial.multiply(sixk.add(BigDecimal.ONE)).multiply(sixk.add(BigDecimal.valueOf(2)))
					.multiply(sixk.add(BigDecimal.valueOf(3))).multiply(sixk.add(BigDecimal.valueOf(4)))
					.multiply(sixk.add(BigDecimal.valueOf(5))).multiply(sixk.add(BigDecimal.valueOf(6)));
			k = k.add(BigDecimal.ONE);

			onefactorial = onefactorial.multiply(k);

		}

		return BigDecimal.ONE.divide(sum, mc).multiply(coeff).round(mc);
	}

	private static BigDecimal highest_acc_e;
	private static MathContext highest_acc_context_e;

	public static final BigDecimal e(final MathContext mc) {

		if (highest_acc_e != null && mc.getPrecision() < highest_acc_context_e.getPrecision()
				|| (mc.getRoundingMode() == mc.getRoundingMode()
						&& mc.getPrecision() == highest_acc_context_e.getPrecision()))
			return highest_acc_e.round(mc);

		if (mc.getPrecision() == highest_acc_context_e.getPrecision()) {
			MathContext oneHigher = new MathContext(mc.getPrecision() + 1, mc.getRoundingMode());
			highest_acc_e = e0(highest_acc_context_e = oneHigher);
			return highest_acc_e.round(mc);
		} else
			return highest_acc_e = e0(highest_acc_context_e = mc);
	}

	/**
	 * Algorithm used to determine e to an arbitrary amount of digits.
	 * <p>
	 * This method utilizes the definition of e to calculate its value:
	 * <p>
	 * <img src=
	 * "https://wikimedia.org/api/rest_v1/media/math/render/svg/698f402ad56c5162a9280b6aaa8eb79acbc49550">
	 * <p>
	 * This algorithm performs well to a point where it is unnecessary to develop an
	 * algorithm based on a more optimized version.
	 * 
	 * @param accuracy
	 * @return e to a specified amount of digits
	 */
	private static final BigDecimal e0(final MathContext mc) {

		BigDecimal e = BigDecimal.valueOf(2);
		BigDecimal preve = null;
		BigDecimal k = BigDecimal.valueOf(2);
		BigDecimal factorial = BigDecimal.valueOf(2);

		while (!e.equals(preve)) {

			preve = e;

			e = e.add(BigDecimal.ONE.divide(factorial, mc)).round(mc);
			factorial = factorial.multiply(k = k.add(BigDecimal.ONE));

		}

		return e;
	}

	public static final BigDecimal phi(final MathContext mc) {
		return ExponentiationUtils.root(BigDecimal.valueOf(5), BigInteger.TWO, mc).add(BigDecimal.ONE)
				.divide(BigDecimal.valueOf(2)).round(mc);
	}
}
