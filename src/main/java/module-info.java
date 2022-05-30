module com.maxsavteam.calculator {
	exports com.maxsavteam.calculator;
	exports com.maxsavteam.calculator.exceptions;
	exports com.maxsavteam.calculator.tree;
	exports com.maxsavteam.calculator.tree.nodes;
	exports com.maxsavteam.calculator.resolvers;
	exports com.maxsavteam.calculator.results;
	exports com.maxsavteam.calculator.utils;

	opens com.maxsavteam.calculator;
	opens com.maxsavteam.calculator.exceptions;
	opens com.maxsavteam.calculator.tree;
	opens com.maxsavteam.calculator.tree.nodes;
	opens com.maxsavteam.calculator.resolvers;
	opens com.maxsavteam.calculator.results;
	opens com.maxsavteam.calculator.utils;

	requires ch.obermuhlner.math.big;
	requires org.jetbrains.annotations;
}