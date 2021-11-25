package soluturus.base.exceptions;

/**
 * Thrown when a Soluturus container constructor is given an illegal argument.
 * 
 * @author Miles K. Bertrand
 * 
 */
public class ExpressionContainmentException extends IllegalArgumentException {

	private static final long serialVersionUID = -3935355402542453642L;

	public ExpressionContainmentException() {
		super();
	}

	public ExpressionContainmentException(String cause) {
		super(cause);
	}
}
