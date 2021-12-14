package soluturus.base.internal.algebraic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import soluturus.base.exceptions.ExpressionContainmentException;
import soluturus.base.expressions.Expression;
import soluturus.base.expressions.Integer;
import soluturus.base.expressions.Variable;
import soluturus.base.internal.InternalAddition;
import soluturus.base.internal.InternalDivision;
import soluturus.base.internal.InternalExponentiation;
import soluturus.base.internal.InternalMultiplication;

/**
 * Represents the sum of n addends wherein addend<sub>1</sub> *
 * addend<sub>2</sub> ...* addend<sub>n</sub> is the fully simplified form. It
 * can be operated on as a single unit and can interact with any other
 * Expression.
 * <p>
 * Sums may not contain Sums in the addend positions.
 * 
 * @author Miles K. Bertrand
 * 
 */
public final record Sum(Expression[] addends) implements Expression, Iterable<Expression> {

	public Sum(Expression... addends) {

		if (addends.length < 2)
			throw new ExpressionContainmentException();

		for (Expression e : addends)
			if (e instanceof Sum || e == null || e.equals(zero))
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
			return InternalAddition.add(a2, this);
		else if (addend instanceof Variable a2)
			return InternalAddition.add(a2, this);
		else
			return InternalAddition.add(this, addend);
	}

	@Override
	public Expression multiply(Expression multiplicand) {
		if (multiplicand instanceof Integer mu2)
			return InternalMultiplication.multiply(mu2, this);
		else if (multiplicand instanceof Variable mu2)
			return InternalMultiplication.multiply(mu2, this);
		else
			return InternalMultiplication.multiply(this, multiplicand);
	}

	@Override
	public Expression divide(Expression divisor) {
		return InternalDivision.divide(this, divisor);
	}

	@Override
	public Expression pow(Expression exponent) {
		return InternalExponentiation.pow(this, exponent);
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
	public Expression derive(Variable v) {
		Expression derivative = zero;
		for (Expression e : addends)
			derivative = derivative.add(e.derive(v));
		return derivative;
	}

	@Override
	public Expression substitute(Variable v, Expression replacement) {
		Expression expr = zero;
		for (Expression e : addends)
			expr = expr.add(e.substitute(v, replacement));
		return expr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Expression[] factor() {
		// TODO Auto-generated method stub

		ArrayList<Expression>[] addendFactors = new ArrayList[addends.length];

		for (int i = 0; i < addends.length; i++)
			addendFactors[i] = new ArrayList<Expression>(Arrays.asList(addends[i].factor()));
		
		ArrayList<Expression> factors = new ArrayList<>();
		for (int i = 0; i < addendFactors[0].size(); i++) {
			
			boolean canRemove = true;
			for (int j = 1; j < addendFactors.length; j++)
				if (!addendFactors[j].contains(addendFactors[0].get(i)))
					canRemove = false;
			
			if (canRemove) {
				factors.add(addendFactors[0].get(i));
				for (int j = 1; j < addendFactors.length; j++)
					addendFactors[j].remove(addendFactors[0].get(i));
				addendFactors[0].remove(addendFactors[0].get(i--));
			}
		}

		// TODO
		if (factors.size() == 0)
			return new Expression[] { this };
		else {
			Expression sum = zero;
			for (ArrayList<Expression> ae : addendFactors)
				sum = sum.add(Expression.product(ae.toArray(new Expression[ae.size()])));

			factors.add(sum);

			return factors.toArray(new Expression[factors.size()]);
		}
	}

	@Override
	public final boolean isKnown() {
		for (Expression e : addends)
			if (!e.isKnown())
				return false;
		return true;
	}

	@Override
	public boolean isMonomial() {
		return false;
	}

	@Override
	public boolean isPolynomial() {
		for (Expression e : addends)
			if (!e.isMonomial())
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
