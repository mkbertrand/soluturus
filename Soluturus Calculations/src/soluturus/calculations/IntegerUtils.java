package soluturus.calculations;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Defines static methods for integer (in the numeric sense) manipulation.
 * <p>
 * Most methods contain methods related to divisibility, such as
 * {@link #factor(BigInteger) factor}, which returns an array of all prime
 * factors of its argument, or {@link #gcd(BigInteger, BigInteger) gcd}, which
 * finds the Greatest Common Divisor of two numbers.
 * 
 * @author Miles K Bertrand
 *
 */
public final class IntegerUtils {

	private IntegerUtils() {
		throw new UnsupportedOperationException();
	}

	public static final BigInteger factorial(final BigInteger number) {
		// TODO improve
		if (number.equals(ZERO) || number.equals(ONE))
			return ONE;
		else {
			BigInteger ttl = ONE;
			for (BigInteger i = BigInteger.TWO; i.compareTo(number) < 1; i = i.add(ONE))
				ttl = ttl.multiply(i);
			return ttl;
		}
	}

	public static final BigInteger nCk(final BigInteger n, final BigInteger k) {
		return factorial(n).divide(factorial(k).multiply(factorial(n.subtract(k))));
	}

	/**
	 * Returns the Greatest Common Divisor of the two arguments a and b.
	 * 
	 * @param a
	 * @param b
	 * @return gcd(a, b)
	 */
	public static final BigInteger gcd(final BigInteger a, final BigInteger b) {
		return b.equals(ZERO) ? a : gcd(b, a.mod(b));
	}

	/**
	 * Returns the Least Common Multiple of the two arguments a and b.
	 * 
	 * @param a
	 * @param b
	 * @return lcm(a, b)
	 */
	public static final BigInteger lcm(final BigInteger a, final BigInteger b) {
		return a.multiply(b).divide(gcd(a, b));
	}

	/**
	 * Returns all prime factors of this number.
	 * <p>
	 * The prime factors of a number are all of the prime numbers which, when
	 * multiplied together, will have a product equal to <code>number</code>.
	 * 
	 * @param number
	 * @return
	 */
	public static final BigInteger[] factor(final BigInteger number) {

		if (number.signum() == 0)
			return new BigInteger[] { ZERO };
		else if (number.equals(ONE))
			return new BigInteger[] { ONE };

		ArrayList<BigInteger> factors = factor0(number);
		return factors.toArray(new BigInteger[factors.size()]);
	}

	private static final ArrayList<BigInteger> factor0(BigInteger number) {

		if (number.signum() == -1) {
			final ArrayList<BigInteger> factorsn = factor0(number.negate());
			factorsn.add(BigInteger.valueOf(-1));
			return factorsn;
		}

		ArrayList<BigInteger> factors = new ArrayList<BigInteger>();

		Iterator<BigInteger> primes = PrimeFinder.DEFAULT_FINDER.iterator();

		while (!number.equals(ONE)) {

			BigInteger cursor = primes.next();

			while (number.remainder(cursor).equals(ZERO)) {
				number = number.divide(cursor);
				factors.add(cursor);
			}
		}

		return factors;
	}

	private static final class PowerCalcNode {

		int occurences;
		BigInteger number;

		PowerCalcNode(BigInteger number) {
			this.number = number;
			occurences = 1;
		}
	}

	public static final BigInteger[] asPower(BigInteger number) {

		if (number.signum() == 0)
			return new BigInteger[] { ZERO, ONE };
		else if (number.equals(ONE))
			return new BigInteger[] { ONE, ONE };

		ArrayList<PowerCalcNode> nodes = new ArrayList<>();

		outer: for (BigInteger b : factor(number)) {
			for (PowerCalcNode p : nodes)
				if (p.number.equals(b)) {
					p.occurences++;
					continue outer;
				}

			nodes.add(new PowerCalcNode(b));
		}

		int gcd = nodes.get(0).occurences;

		for (PowerCalcNode p : nodes)
			gcd = (int) PrimitiveMath.gcd(gcd, p.occurences);

		BigInteger base = ONE;

		for (PowerCalcNode p : nodes)
			base = base.multiply(p.number.pow(p.occurences / gcd));

		return new BigInteger[] { base, BigInteger.valueOf(gcd) };

	}
}
