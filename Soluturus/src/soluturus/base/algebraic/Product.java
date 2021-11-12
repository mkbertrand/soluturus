package soluturus.base.algebraic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import soluturus.base.ExpressionContainmentException;
import soluturus.base.exposed.Expression;
import soluturus.base.internal.SoluturusAddition;
import soluturus.base.internal.SoluturusMath;
import soluturus.base.internal.SoluturusMultiplication;

public final record Product(Expression[] factors) implements Expression {

	public Product(Expression... factors) {

		if (factors.length < 2)
			throw new ExpressionContainmentException();

		for (Expression e : factors)
			if (e instanceof Sum || e instanceof Product)
				throw new ExpressionContainmentException();
		this.factors = factors;
	}

	public final Expression[] factors() {
		return factors.clone();
	}

	public final int length() {
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
		System.out.println("dividing");
		return SoluturusMath.divide(this, divisor);
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
	public final boolean isKnown() {
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
}
