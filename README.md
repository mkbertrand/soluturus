# soluturus
Hi!
Soluturus is a symbolic algebra program designed to store expressions and perform operations with those expressions.  These expressions may be of known or unknown value and are able to have a flexible shape.  Soluturus does this by nesting classes that represent various forms.  All classes used to represent operations are immutable.

The soluturus.base.expressions.Expression interface is implemented by all objects within the Soluturus library which are designed to store fully simplified expressions. Expression implementations must implement the following methods:

	clone()
	
	add(Expression addend)
	
	multiply(Expression multiplicand)
	
	divide(Expression divisor)
	
	pow(Expression exponent)
	
	log(Expression base)
	
	negate()
	
	reciprocate()
	
	sin()
	
	substitute(Variable v, Expression replacement)
	
	isKnown()
	
In addition, the following methods may be implemented or be defined due to the class extending java.lang.Record:
	[any getters]
	equals(Object o)
	subtract(Expression subtrahend)
	root(Expression exponent)
	ln()
Do note that all Expressions are able to be used interchangeably with other Expressions that have been implemented within the library and must support certain operations.
