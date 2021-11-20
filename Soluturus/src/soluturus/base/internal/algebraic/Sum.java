package soluturus.base.internal.algebraic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import soluturus.base.ExpressionContainmentException;
import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Integer;
import soluturus.base.expressions.Variable;
import soluturus.base.internal.SoluturusAddition;
import soluturus.base.internal.SoluturusExponentiation;
import soluturus.base.internal.SoluturusMultiplication;

/**
 * Represents the sum of n addends wherein addend<sub>1</sub> *
 * addend<sub>2</sub> ...* addend<sub>n</sub> is the fully simplified form. It
 * can be operated on as a single unit and can interact with any other
 * Expression.
 * 
 * @author Miles K. Bertrand
 * 
 */
public final record Sum(Expression[] addends) implements Expression, Iterable<Expression> {

	public Sum(Expression... addends) {

		if (addends.length < 2)
			throw new ExpressionContainmentException();

		for (Expression e : addends)
			if (e instanceof Sum || e == null)
				throw new ExpressionContainmentException(e.toString());
		this.addends = addends;
	}

	public Sum(List<Expression> addends) {
		this(addends.toArray(new Expression[addends.size()]));
	}

	public final Expression[] addends() {
		return addends.clone();
	}

	@Override
	public Iterator<Expression> iterator() {
		return Arrays.asList(addends).iterator();
	}

	public final int length() {
		return addends.length;
	}

	@Override
	public Sum clone() {
		return new Sum(addends());
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Sum other))
			return false;
		else if (length() != other.length())
			return false;

		ArrayList<Expression> addends1 = new ArrayList<>(Arrays.asList(addends));
		List<Expression> addends2 = Arrays.asList(other.addends);

		addends1.removeIf(addends2::contains);

		return addends1.size() == 0;
	}

	@Override
	public Expression add(Expression addend) {
		if (addend instanceof Integer a2)
			return SoluturusAddition.add(a2, this);
		else if (addend instanceof Variable a2)
			return SoluturusAddition.add(a2, this);
		else
			return SoluturusAddition.add(this, addend);
	}

	@Override
	public Expression multiply(Expression multiplicand) {
		if (multiplicand instanceof Integer mu2)
			return SoluturusMultiplication.multiply(mu2, this);
		else if (multiplicand instanceof Variable mu2)
			return SoluturusMultiplication.multiply(mu2, this);
		else
			return SoluturusMultiplication.multiply(this, multiplicand);
	}

	@Override
	public Expression divide(Expression dividend) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression pow(Expression exponent) {
		return SoluturusExponentiation.pow(this, exponent);
	}

	@Override
	public Expression log(Expression base) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression negate() {
		Expression[] result = new Expression[addends.length];
		for (int i = 0; i < addends.length; i++)
			result[i] = addends[i].negate();
		return new Sum(result);
	}

	@Override
	public Expression reciprocate() {
		return new Power(this, negative_one);
	}

	@Override
	public Expression sin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression substitute(Variable v, Expression replacement) {
		Expression expr = zero;
		for (Expression e : addends)
			expr = expr.add(e.substitute(v, replacement));
		return expr;
	}

	@Override
	public Expression[] factor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final boolean isKnown() {
		for (Expression e : addends)
			if (!e.isKnown())
				return false;
		return true;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder(addends[0].toString());

		String end;
		for (int i = 1; i < addends.length; i++)
			sb.append((end = addends[i].toString()).charAt(0) == '-' ? end : "+" + end);
		return sb.toString();
	}
}
