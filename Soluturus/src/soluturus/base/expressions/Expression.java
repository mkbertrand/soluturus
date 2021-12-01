package soluturus.base.expressions;

import java.io.Serializable;
import java.math.BigInteger;

import soluturus.utils.internal.Simplifier;

/**
 * 
 * An object that stores a mathematical expression.
 * <p>
 * 
 * This is the root of all types that hold an expression, whether constant or
 * variable; single or multi-term; even real or imaginary.
 * <p>
 * 
 * <head><b>Commonalities Between Expression Implementations</b></head>
 * <p>
 * All Expression implementations share certain characteristics that allow them
 * to be useful practically:
 * <ol>
 * <li><b>A Expression will always be immutable.</b>
 * 
 * <pre></pre>
 * 
 * <li><b>Its constructors will always standardize mathematically equal
 * expressions to the same form.</b>
 * <p>
 * All expressions will always be stored in their most simplified form.
 * <p>
 * <small>For example, 2<sup>2</sup> and 16<sup>1/2</sup> will both simplify to
 * 4.</small>
 * <p>
 * The benefits of standardization include realistically viable equals()
 * methods, lower operation cost, and smaller file size.
 * 
 * <pre></pre>
 * 
 * <li><b>Operations, controlling for arguments, will always yield the same
 * result.</b>
 * <p>
 * No Expression will have a result that changes based on amount of calls, time,
 * date, etc.
 * 
 * <pre></pre>
 * 
 * <head><b>Encapsulation of Expression Implementations</b></head>
 * <p>
 * Most Expression implementations are not exported, and the Soluturus module is
 * not open. This is done for numerous reasons:
 * <ol>
 * <li>It forces interchangeable usage by the end user.
 * <li>It decreases the amount of input checks performed by the API because
 * there is a limited route for inflow of information, easily allowing audits
 * where necessary. If all Expression implementations were exported, their
 * constructors would have to check the input and would have to contain
 * conversion functions.
 * <li>It prevents a large portion of reflective modifications on Expression
 * implementations, because the bulk of the information stored in a expression
 * is in the class, as opposed to the few fields specified by Expression itself.
 * <li>It forces the end user to use methods designed for external interaction
 * rather than internal, unsafe methods.
 * 
 * @author Miles K. Bertrand
 *
 */
public interface Expression extends Cloneable, Serializable {

	public static final Variable pi = new Variable("π");
	public static final Variable e = new Variable("e");

	public static final Integer negative_one = Integer.of(-1);
	public static final Integer zero = Integer.of(0);
	public static final Integer one = Integer.of(1);
	public static final Expression one_half = of("1/2");
	public static final Integer two = Integer.of(2);
	public static final Integer ten = Integer.of(10);

	public static final Expression i = negative_one.pow(one_half);

	public static Integer of(long l) {
		return Integer.of(l);
	}

	public static Integer of(BigInteger b) {
		return Integer.of(b);
	}

	public static Expression of(char c) {
		if (Character.isDigit(c))
			return Integer.of((int) (c - '0'));
		return new Variable(c);
	}

	public static Expression of(String s) {
		return Simplifier.parse(s);
	}

	public Expression clone();

	public Expression add(Expression addend);

	public default Expression add(Expression... addends) {

		Expression sum = this;

		for (int i = 0; i < addends.length; i++)
			sum = sum.add(addends[i]);

		return sum;
	}

	public default Expression subtract(Expression subtrahend) {
		return add(subtrahend.negate());
	}

	public Expression multiply(Expression multiplicand);

	public default Expression multiply(Expression... multiplicands) {

		Expression product = this;

		for (Expression e : multiplicands)
			product = product.multiply(e);

		return product;
	}

	public Expression divide(Expression dividend);

	public Expression pow(Expression exponent);

	public default Expression root(Expression exponent) {
		return pow(exponent.reciprocate());
	}

	public Expression log(Expression base);

	public default Expression ln() {
		return log(e);
	}

	public Expression negate();

	public Expression reciprocate();

	public Expression sin();

	public Expression substitute(Variable v, Expression replacement);

	public Expression[] factor();

	public boolean isKnown();
}
