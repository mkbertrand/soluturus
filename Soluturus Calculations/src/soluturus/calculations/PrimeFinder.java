package soluturus.calculations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Object used to find the next prime based on the previous known primes.
 * <p>
 * Stores an ArrayList of all previous known primes, which is uses to check
 * divisibility of candidate primes.
 * <p>
 * The next function works in O(√(n) time.
 * 
 * @author Miles K Bertrand
 *
 */
public final class PrimeFinder implements Cloneable, Iterable<BigInteger>, Serializable {

	public static final PrimeFinder DEFAULT_FINDER = new PrimeFinder();
	
	private static final long serialVersionUID = 1307057860368995251L;

	private final class Itr implements Iterator<BigInteger> {

		private int cursor = -1;

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public BigInteger next() {
			return ++cursor >= primes.size() ? PrimeFinder.this.next() : primes.get(cursor);
		}

		@Override
		public void forEachRemaining(Consumer<? super BigInteger> action) {
			throw new UnsupportedOperationException("forEachRemaining");
		}
	}

	private BigInteger cursor;
	private final ArrayList<BigInteger> primes;

	public PrimeFinder() {
		cursor = BigInteger.ONE;
		primes = new ArrayList<BigInteger>();
	}

	public synchronized BigInteger next() {

		boolean prime;

		do {

			prime = true;
			cursor = cursor.add(BigInteger.ONE);
			int i = 0;

			while (prime && i < primes.size() && primes.get(i).pow(2).compareTo(cursor) != 1)
				if (cursor.mod(primes.get(i++)).equals(BigInteger.ZERO))
					prime = false;

		} while (!prime);

		primes.add(cursor);
		return cursor;
	}

	public BigInteger current() {
		return primes.get(primes.size() - 1);
	}

	public ArrayList<BigInteger> getPrimes() {
		return primes;
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		cursor = primes.get(primes.size() - 1);
	}

	@Override
	public Iterator<BigInteger> iterator() {
		return new Itr();
	}
}
