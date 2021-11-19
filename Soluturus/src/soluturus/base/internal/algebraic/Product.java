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
import soluturus.base.internal.SoluturusDivision;
import soluturus.base.internal.SoluturusMultiplication;

/**
 * Represents the product of n factors wherein factor<sub>1</sub> *
 * factor<sub>2</sub> ...* factor<sub>n</sub> is the fully simplified form. It
 * can be operated on as a single unit and can interact with any other
 * Expression.
 * 
 * @author Miles K. Bertrand
 * 
 */
public final record Product(Expression[] factors) implements Expression, Iterable<Expression> {

	public Product(Expression... factors) {

		if (factors.length < 2)
			throw new ExpressionContainmentException();

		for (Expression e : factors)
			if (e instanceof Sum || e instanceof Product || e.equals(one))
				throw new ExpressionContainmentException();
		this.factors = factors;
	}

	public Product(List<Expression> factors) {
		this(factors.toArray(new Expression[factors.size()]));
	}

	public Expression[] factors() {
		return factors.clone();
	}

	public int length() {
		return factors.length;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Product other))
			return false;

		ArrayList<Expression> thesefactors = new ArrayList<>(Arrays.asList(factors));
		List<Expression> thosefactors = Arrays.asList(other.factors);

		thesefactors.removeIf(thosefactors::contains);

		return thesefactors.size() == 0;
	}

	@Override
	public Expression add(Expression addend) {
		if (addend instanceof Integer a2)
			return SoluturusAddition.add(a2, this);
		else if (addend instanceof Variable a2)
			return SoluturusAddition.add(a2, this);
		else if (addend instanceof Sum a2)
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
		else if (multiplicand instanceof Sum mu2)
			return SoluturusMultiplication.multiply(mu2, this);
		else
			return SoluturusMultiplication.multiply(this, multiplicand);
	}

	@Override
	public Expression divide(Expression divisor) {
		return SoluturusDivision.divide(this, divisor);
	}

	@Override
	public Expression pow(Expression exponent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression log(Expression base) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression negate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression reciprocate() {
		Expression result = one;
		for (Expression e : factors)
			result = result.multiply(e.reciprocate());
		return result;
	}

	@Override
	public Expression sin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression substitute(Variable v, Expression replacement) {
		Expression expr = one;
		for (Expression e : factors)
			expr = expr.multiply(e.substitute(v, replacement));
		return expr;
	}

	@Override
	public Expression[] factor() {

		Expression[][] factors = new Expression[length()][];

		for (int i = 0; i < length(); i++)
			factors[i] = this.factors[i].factor();

		int lengthflat = 0;

		for (Expression[] e : factors)
			lengthflat += e.length;

		Expression[] factorsflat = new Expression[lengthflat];

		int index = 0;
		for (int i = 0; i < factors.length; i++)
			for (int j = 0; j < factors[i].length; j++)
				factorsflat[index++] = factors[i][j];

		return factorsflat;
	}

	@Override
	public boolean isKnown() {
		for (Expression e : factors)
			if (!e.isKnown())
				return false;
		return true;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder(factors[0].toString());

		for (int i = 1; i < factors.length; i++)
			sb.append("*" + factors[i].toString());

		return sb.toString();
	}

	@Override
	public Iterator<Expression> iterator() {
		return Arrays.asList(factors).iterator();
	}
}
