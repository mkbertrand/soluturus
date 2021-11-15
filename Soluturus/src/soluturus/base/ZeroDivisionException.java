package soluturus.base;

/**
 * Thrown when a method attempts to divide by zero.
 * 
 * @author Miles K. Bertrand
 * 
 */
public class ZeroDivisionException extends ArithmeticException {

	private static final long serialVersionUID = -6213316378633013147L;

	public ZeroDivisionException() {
		super();
	}

	public ZeroDivisionException(String cause) {
		super(cause);
	}
}