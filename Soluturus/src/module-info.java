module soluturus.base {

	requires java.base;
	requires soluturus.calculations;

	exports soluturus.base;
	exports soluturus.base.algebraic;
	exports soluturus.base.exposed;
	exports soluturus.base.trigonometric;

}