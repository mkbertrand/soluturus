package soluturus.base;

public class ZeroDivisionException extends ArithmeticException {

	private static final long serialVersionUID = -6213316378633013147L;

	public ZeroDivisionException() {
		super();
	}

	public ZeroDivisionException(String cause) {
		super(cause);
	}
}