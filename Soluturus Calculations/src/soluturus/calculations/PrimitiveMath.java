package soluturus.calculations;

public final class PrimitiveMath {

	private PrimitiveMath() {
		throw new UnsupportedOperationException();
	}

	public static final long gcd(final long a, final long b) {
		return b == 0 ? a : gcd(b, a % b);
	}
}
