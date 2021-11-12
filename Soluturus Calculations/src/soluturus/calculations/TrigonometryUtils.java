package soluturus.calculations;

import static java.math.BigDecimal.ONE;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public final class TrigonometryUtils {

	private TrigonometryUtils() {
		throw new UnsupportedOperationException();
	}

	public static BigDecimal acos(final BigDecimal number, final MathContext mc) {

		if (number.abs().compareTo(ONE) == 1)
			throw new ArithmeticException("Illegal acos(x) for x > 1: x = " + number);

		MathContext mc2 = new MathContext(mc.getPrecision() + 6, mc.getRoundingMode());

		return IrrationalUtils.pi(mc2).divide(BigDecimal.valueOf(2), mc2).subtract(asin(number, mc2)).round(mc);
	}

	private static final Object asinlock = new Object();

	private static int nasin = 0;
	private static Fraction factorial2n = Fraction.ONE;
	private static Fraction factorialN = Fraction.ONE;
	private static Fraction fourPowerN = Fraction.ONE;
	private static final List<Fraction> asinfactors = new ArrayList<>();

	public static BigDecimal asin(BigDecimal number, MathContext mc) {

		if (number.abs().compareTo(ONE) == 1)
			throw new ArithmeticException();

		if (number.signum() == -1)
			return asin(number.negate(), mc).negate();

		MathContext mc2 = new MathContext(mc.getPrecision() + 6, mc.getRoundingMode());

		if (number.compareTo(BigDecimal.valueOf(0.707107)) >= 0) {
			return acos(ExponentiationUtils.root(ONE.subtract(number.multiply(number)), BigInteger.TWO, mc2), mc);
		}

		BigDecimal acceptableError = BigDecimal.ONE.movePointLeft(mc2.getPrecision() + 1);

		final BigDecimal xPowerTwo = number.multiply(number, mc2);
		BigDecimal powerOfX = number;

		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal step;
		int i = 0;
		do {
			Fraction factor;

			synchronized (asinlock) {
				while (asinfactors.size() <= i) {
					asinfactors.add((factorial2n.divide(fourPowerN.multiply(factorialN).multiply(factorialN)
							.multiply(new Fraction(2 * nasin++ + 1)))));
					factorial2n = (Fraction) factorial2n.multiply(new Fraction(4 * nasin * (nasin - .5)));
					factorialN = (Fraction) factorialN.multiply(new Fraction(nasin));
					fourPowerN = (fourPowerN.multiply(Fraction.FOUR));
				}
			}

			factor = asinfactors.get(i);
			step = new BigDecimal(factor.getNumerator()).multiply(powerOfX)
					.divide(new BigDecimal(factor.getDenominator()), mc2);
			i++;
			powerOfX = powerOfX.multiply(xPowerTwo, mc2);

			sum = sum.add(step);

		} while (step.abs().compareTo(acceptableError) > 0);

		return sum.round(mc);
	}

	public static BigDecimal atan(final BigDecimal number, final MathContext mc) {

		MathContext mc2 = new MathContext(mc.getPrecision() + 6, mc.getRoundingMode());

		return asin(number.divide(ExponentiationUtils.root(number.multiply(number).add(ONE), BigInteger.TWO, mc2)), mc2)
				.round(mc);
	}

	public static final BigDecimal cos(final BigDecimal number, final MathContext mc) {
		return sin(IrrationalUtils.pi(mc).divide(BigDecimal.valueOf(2)).subtract(number), mc);
	}

	private static final Object sinlock = new Object();

	private static int nsin = 0;
	private static Fraction factorial2nPlus1 = Fraction.ONE;
	private static final List<Fraction> sinfactors = new ArrayList<>();

	public static BigDecimal sin(BigDecimal number, final MathContext mc) {

		if (mc.getPrecision() == 0)
			throw new IllegalArgumentException();

		MathContext mc2 = new MathContext(mc.getPrecision() + 6, mc.getRoundingMode());

		if (number.abs().compareTo(IrrationalUtils.pi(mc2)) > 0)
			number = number.remainder(
					BigDecimal.valueOf(2).multiply(
							IrrationalUtils.pi(new MathContext(mc2.getPrecision() + 4, mc.getRoundingMode()))),
					new MathContext(mc2.getPrecision() + 4, mc.getRoundingMode()));

		final BigDecimal xPowerTwo = number.multiply(number, mc2);

		BigDecimal powerOfX = number;

		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal step;

		int i = 0;

		do {

			synchronized (sinlock) {
				while (sinfactors.size() <= i) {

					sinfactors.add(factorial2nPlus1);

					factorial2nPlus1 = (Fraction) factorial2nPlus1.divide(new Fraction(4 * ++nsin * nsin + 2 * nsin));
				}
			}

			step = new BigDecimal(sinfactors.get(i).getNumerator()).multiply(powerOfX)
					.divide(new BigDecimal(sinfactors.get(i).getDenominator()), mc2);
			powerOfX = powerOfX.multiply(xPowerTwo, mc2);

			i++;

			synchronized (sinlock) {
				while (sinfactors.size() <= i) {

					sinfactors.add(factorial2nPlus1.additiveInverse());

					factorial2nPlus1 = (Fraction) factorial2nPlus1.divide(new Fraction(4 * ++nsin * nsin + 2 * nsin));
				}
			}

			BigDecimal step2 = new BigDecimal(sinfactors.get(i).getNumerator()).multiply(powerOfX)
					.divide(new BigDecimal(sinfactors.get(i).getDenominator()), mc2);
			powerOfX = powerOfX.multiply(xPowerTwo, mc2);

			step = step.add(step2);
			i++;

			sum = sum.add(step);
			// System.out.println(sum + " " + step);
		} while (step.abs().compareTo(BigDecimal.ONE.movePointLeft(mc2.getPrecision() + 1)) > 0);

		return sum.round(mc);
	}

	public static final BigDecimal tan(final BigDecimal number, final MathContext mc) {
		return sin(number, mc).divide(cos(number, mc), mc).round(mc);
	}
}
