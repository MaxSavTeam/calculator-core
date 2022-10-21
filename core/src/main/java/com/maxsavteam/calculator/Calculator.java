/*
 * Copyright (C) 2022 MaxSav Team
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.maxsavteam.calculator;

import com.maxsavteam.calculator.exceptions.CalculationException;
import com.maxsavteam.calculator.resolvers.BinaryOperatorResolver;
import com.maxsavteam.calculator.resolvers.BracketsResolver;
import com.maxsavteam.calculator.resolvers.ConstantsResolver;
import com.maxsavteam.calculator.resolvers.FunctionsResolver;
import com.maxsavteam.calculator.resolvers.ListFunctionsResolver;
import com.maxsavteam.calculator.resolvers.SuffixOperatorResolver;
import com.maxsavteam.calculator.results.BaseResult;
import com.maxsavteam.calculator.results.Number;
import com.maxsavteam.calculator.results.NumberList;
import com.maxsavteam.calculator.tree.BinaryOperator;
import com.maxsavteam.calculator.tree.BracketsType;
import com.maxsavteam.calculator.tree.SuffixOperator;
import com.maxsavteam.calculator.tree.TreeBuilder;
import com.maxsavteam.calculator.tree.nodes.BracketsNode;
import com.maxsavteam.calculator.tree.nodes.ConstantNode;
import com.maxsavteam.calculator.tree.nodes.FunctionNode;
import com.maxsavteam.calculator.tree.nodes.ListNode;
import com.maxsavteam.calculator.tree.nodes.NegativeNumberNode;
import com.maxsavteam.calculator.tree.nodes.NumberNode;
import com.maxsavteam.calculator.tree.nodes.OperatorNode;
import com.maxsavteam.calculator.tree.nodes.SuffixOperatorNode;
import com.maxsavteam.calculator.tree.nodes.TreeNode;
import com.maxsavteam.calculator.utils.CalculatorUtils;
import com.maxsavteam.calculator.utils.MathUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Calculator {

	public static final String PI_SIGN = "\u03C0";
	public static final String FI_SIGN = "\u03C6";
	public static final String E_SIGN = "\u0190";
	public static final String DEGREE_SIGN = "\u00B0";
	public static final String GRAD_SIGN = "\u1DA2";

	private final TreeBuilder builder;
	private final CalculatorExpressionTokenizer expressionTokenizer;
	private final CalculatorExpressionFormatter expressionFormatter;
	private static int roundScale = 8;
	private BinaryOperatorResolver resolver = defaultResolver;
	private BracketsResolver bracketsResolver = defaultBracketsResolver;
	private FunctionsResolver functionsResolver = defaultFunctionsResolver;
	private ListFunctionsResolver listFunctionsResolver = defaultListFunctionResolver;
	private SuffixOperatorResolver suffixResolver = defaultSuffixResolver;
	private ConstantsResolver constantsResolver = defaultConstantsResolver;
	private char decimalSeparator = DecimalFormatSymbols.getInstance(Locale.ROOT).getDecimalSeparator();
	private char groupingSeparator = DecimalFormatSymbols.getInstance(Locale.ROOT).getGroupingSeparator();

	public static final Map<String, String> defaultReplacementMap = new HashMap<>();

	public static final BinaryOperatorResolver defaultResolver = new BinaryOperatorResolver() {
		@Override
		public @NotNull BigDecimal calculate(char operator, BigDecimal a, BigDecimal b) {
			if (operator == '+')
				return a.add(b);
			if (operator == '-')
				return a.subtract(b);
			if (operator == '*')
				return a.multiply(b);
			if (operator == '/') {
				if (b.signum() == 0)
					throw new CalculationException(CalculationException.DIVISION_BY_ZERO);
				else
					return a.divide(b, roundScale, RoundingMode.HALF_EVEN);
			}
			if (operator == '^')
				return MathUtils.pow(a, b);
			throw new CalculationException(CalculationException.INVALID_BINARY_OPERATOR);
		}

		@Override
		public @NotNull BigDecimal calculatePercent(char binaryOperator, BigDecimal a, BigDecimal percent) {
			BigDecimal percentOfNum = a.multiply(percent); // percent already divided by zero
			if (binaryOperator == '+')
				return a.add(percentOfNum);
			else if (binaryOperator == '-')
				return a.subtract(percentOfNum);
			else if (binaryOperator == '*')
				return percentOfNum;
			else if (binaryOperator == '/') {
				if (percent.signum() == 0)
					throw new CalculationException(CalculationException.DIVISION_BY_ZERO);
				else
					return a.divide(percent, roundScale, RoundingMode.HALF_EVEN);
			}
			throw new CalculationException(CalculationException.INVALID_OPERATOR_FOR_PERCENT);
		}
	};

	public static final ConstantsResolver defaultConstantsResolver = constantName -> {
		switch (constantName) {
			case "pi":
			case PI_SIGN:
				return NumberList.of(MathUtils.PI);
			case "fi":
			case FI_SIGN:
				return NumberList.of(MathUtils.FI);
			case E_SIGN:
				return NumberList.of(MathUtils.E);
			default:
				return null;
		}
	};

	public static final FunctionsResolver defaultFunctionsResolver = (funcName, suffix, operand) -> {
		if (suffix == null && operand == null) {
			throw new CalculationException(CalculationException.FUNCTION_SUFFIX_AND_OPERAND_NULL);
		}
		BigDecimal notNullNum = suffix == null ? operand : suffix;
		switch (funcName) {
			case "log":
				if (suffix != null) {
					if (operand != null)
						return MathUtils.logWithBase(operand, suffix);
					else
						return MathUtils.log(suffix);
				} else {
					return MathUtils.log(operand);
				}
			case "cos":
				return MathUtils.cos(notNullNum);
			case "arccos":
			case "acos":
				return MathUtils.arccos(notNullNum);
			case "sin":
				return MathUtils.sin(notNullNum);
			case "arcsin":
			case "asin":
				return MathUtils.arcsin(notNullNum);
			case "tan":
			case "tg":
				return MathUtils.tan(notNullNum);
			case "arctan":
			case "arctg":
			case "atan":
			case "atg":
				return MathUtils.arctan(notNullNum);
			case "ctg":
			case "cot":
				return MathUtils.cot(notNullNum);
			case "arccot":
			case "arcctg":
			case "acot":
			case "actg":
				return MathUtils.arccot(notNullNum);
			case "ln":
				return MathUtils.ln(notNullNum);
			case "sqrt":
				if(suffix != null && operand != null){
					return MathUtils.rootWithBase(operand, suffix);
				}
				return MathUtils.rootWithBase(notNullNum, BigDecimal.valueOf(2));
			case "abs":
				return MathUtils.abs(notNullNum);
			default:
				return null;
		}
	};

	public static final ListFunctionsResolver defaultListFunctionResolver = (funcName, suffix, list) -> {
		NumberList inlinedList = inlineElementsInList(list);
		BigDecimal[] decimals = inlinedList.getResults().stream()
				.filter(Number.class::isInstance)
				.map(b -> ((Number) b).get())
				.toArray(BigDecimal[]::new);

		switch (funcName) {
			case "A": {
				BigDecimal sum = BigDecimal.ZERO;
				for (var d : decimals)
					sum = sum.add(d);
				return NumberList.of(sum.divide(BigDecimal.valueOf(decimals.length), roundScale, RoundingMode.HALF_EVEN));
			}
			case "gcd": {
				return NumberList.of(MathUtils.gcd(decimals));
			}
			case "lcm": {
				return NumberList.of(MathUtils.lcm(decimals));
			}
			case "sum": {
				BigDecimal sum = BigDecimal.ZERO;
				for (var d : decimals)
					sum = sum.add(d);
				return NumberList.of(sum);
			}
			default: {
				return resolveList(list, b -> {
					BigDecimal res = defaultFunctionsResolver.resolve(funcName, suffix, b);
					if(res == null)
						throw new CalculationException(CalculationException.UNKNOWN_FUNCTION, funcName);
					return res;
				});
			}
		}
	};

	private static NumberList inlineElementsInList(NumberList l) {
		ArrayList<BaseResult> results = new ArrayList<>();
		for (var b : l.getResults()) {
			if (b instanceof Number) {
				results.add(b);
			} else if (b instanceof NumberList) {
				results.addAll(inlineElementsInList((NumberList) b).getResults());
			}
		}
		return new NumberList(results);
	}

	private interface ApplierForEachElement {
		BigDecimal apply(BigDecimal a);
	}

	private static NumberList resolveList(NumberList r, ApplierForEachElement applier) {
		if (r.isSingleNumber()) {
			return NumberList.of(applier.apply(r.getSingleNumberIfTrue()));
		} else {
			ArrayList<BaseResult> resultsList = new ArrayList<>();
			for (var b : r.getResults()) {
				if (b instanceof Number) {
					resultsList.add(new Number(applier.apply(((Number) b).get())));
				} else if (b instanceof NumberList) {
					resultsList.add(resolveList((NumberList) b, applier));
				} else {
					resultsList.add(b);
				}
			}
			return new NumberList(resultsList);
		}
	}

	public static final BracketsResolver defaultBracketsResolver = (type, a) -> {
		if (type == 1)
			return a;
		else if (type == 2)
			return MathUtils.round(a);
		else if (type == 3)
			return MathUtils.floor(a);
		else if (type == 4)
			return MathUtils.ceil(a);
		throw new CalculationException(CalculationException.UNKNOWN_BRACKET_TYPE);
	};

	public static final SuffixOperatorResolver defaultSuffixResolver = (operator, count, operand) -> {
		String symbol = operator.getSymbol();
		switch (symbol) {
			case "!":
				return MathUtils.fact(operand, count);
			case "%":
				return operand.multiply(MathUtils.pow(BigDecimal.valueOf(0.01), BigDecimal.valueOf(count)));
			case DEGREE_SIGN:
				BigDecimal degrees = operand;
				for(int i = 0; i < count; i++)
					degrees = AngleUnit.degrees(degrees).toRadians();
				return degrees;
			case GRAD_SIGN:
				BigDecimal grads = operand;
				for(int i = 0; i < count; i++)
					grads = AngleUnit.gradians(grads).toRadians();
				return grads;
		}
		throw new CalculationException(CalculationException.UNKNOWN_SUFFIX_OPERATOR);
	};

	public Calculator() {
		builder = new TreeBuilder();

		expressionFormatter = new CalculatorExpressionFormatter();
		expressionFormatter.setBracketsTypes(TreeBuilder.defaultBrackets);
		expressionFormatter.setSuffixOperators(TreeBuilder.defaultSuffixOperators);

		expressionTokenizer = new CalculatorExpressionTokenizer();
		expressionTokenizer.setReplacementMap(defaultReplacementMap);
	}

	/**
	 * Sets brackets for TreeBuilder
	 **/
	public void setBracketsTypes(List<BracketsType> brackets) {
		builder.setBracketsTypes(brackets);
		expressionFormatter.setBracketsTypes(brackets);
	}

	/**t
	 * Sets binary operators for TreeBuilder
	 **/
	public void setBinaryOperators(List<BinaryOperator> operators) {
		builder.setBinaryOperators(operators);
	}

	/**
	 * Sets suffix operators for TreeBuilder
	 **/
	public void setSuffixOperators(List<SuffixOperator> operators) {
		builder.setSuffixOperators(operators);
		expressionFormatter.setSuffixOperators(operators);
	}

	/**
	 * Sets custom binary operators resolver
	 */
	public void setBinaryOperatorResolver(BinaryOperatorResolver resolver) {
		this.resolver = resolver;
	}

	/**
	 * Sets custom brackets resolver
	 */
	public void setBracketsResolver(BracketsResolver bracketsResolver) {
		this.bracketsResolver = bracketsResolver;
	}

	/**
	 * Sets custom functions resolver
	 */
	public void setFunctionsResolver(FunctionsResolver functionsResolver) {
		this.functionsResolver = functionsResolver;
	}

	/**
	 * Sets custom functions resolver for lists
	 */
	public void setListFunctionsResolver(ListFunctionsResolver listFunctionsResolver) {
		this.listFunctionsResolver = listFunctionsResolver;
	}

	/**
	 * Sets custom suffix operators resolver
	 */
	public void setSuffixResolver(SuffixOperatorResolver suffixResolver) {
		this.suffixResolver = suffixResolver;
	}

	/**
	 * Sets custom aliases for tokenizer
	 */
	public void setAliases(Map<String, String> map) {
		expressionTokenizer.setReplacementMap(map);
	}

	public void setDecimalSeparator(char decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}

	public void setGroupingSeparator(char groupingSeparator) {
		this.groupingSeparator = groupingSeparator;
	}

	public static void setRoundScale(int roundScale) {
		Calculator.roundScale = roundScale;
		MathUtils.setHighRoundScale((int) (roundScale * 1.5));
	}

	/**
	 * Sets custom constants resolver
	 */
	public void setConstantsResolver(ConstantsResolver constantsResolver) {
		this.constantsResolver = constantsResolver;
	}

	public String formatExpression(String expression){
		String expr = expression;
		expr = expr.replace(String.valueOf(groupingSeparator), "");
		if (decimalSeparator != '.')
			expr = expr.replace(decimalSeparator, '.');
		expr = expressionFormatter.removeSpaces(expr);
		expr = expressionTokenizer.tokenizeExpression(expr);
		expr = expressionFormatter.tryToCloseExpressionBrackets(expr);
		expr = expressionFormatter.formatNearBrackets(expr);
		return expr;
	}

	/**
	 * Calculates answer of expression+
	 */
	public NumberList calculate(String expression) {
		TreeNode head = builder.buildTree(formatExpression(expression));
		NumberList r = calc(head);
		return formatAnswer(r);
	}

	private NumberList formatAnswer(NumberList r) {
		ArrayList<BaseResult> n = new ArrayList<>();
		for (var b : r.getResults()) {
			if (b instanceof NumberList) {
				n.add(formatAnswer((NumberList) b));
			} else if (b instanceof Number) {
				BigDecimal a = ((Number) b).get();
				a = CalculatorUtils.removeZeros(a);
				if (a.scale() > roundScale)
					a = a.setScale(roundScale, RoundingMode.HALF_EVEN);
				n.add(new Number(a));
			}
		}
		return new NumberList(n);
	}

	private BigDecimal parseDecimal(String source) {
		try {
			return new BigDecimal(source);
		} catch (NumberFormatException e) {
			throw new CalculationException(CalculationException.NUMBER_FORMAT_EXCEPTION, e);
		}
	}

	private NumberList calc(TreeNode node) {
		if (node instanceof BracketsNode) {
			NumberList r = calc(node.getFirstChild());
			int type = ((BracketsNode) node).getType();
			return resolveList(r, a -> bracketsResolver.resolve(type, a));
		} else if (node instanceof NumberNode) {
			return NumberList.of(parseDecimal(((NumberNode) node).getNumber()));
		} else if (node instanceof NegativeNumberNode) {
			return NegativeNumberNode.apply(calc(node.getFirstChild()));
		} else if (node instanceof FunctionNode) {
			return processFunction((FunctionNode) node);
		} else if (node instanceof SuffixOperatorNode) {
			SuffixOperatorNode suffixNode = (SuffixOperatorNode) node;
			if (suffixNode.getFirstChild() == null)
				throw new CalculationException(CalculationException.NO_OPERAND_FOR_SUFFIX_OPERATOR);
			return resolveList(calc(node.getFirstChild()), a -> resolveSuffix(suffixNode, a));
		} else if (node instanceof OperatorNode) {
			return processOperatorNode((OperatorNode) node);
		} else if (node instanceof ListNode) {
			ListNode listNode = (ListNode) node;
			ArrayList<BaseResult> results = new ArrayList<>();
			for (TreeNode treeNode : listNode.getNodes()) {
				NumberList r = calc(treeNode);
				if (r.isSingleNumber()) {
					results.add(new Number(r.getSingleNumberIfTrue()));
				} else {
					results.add(r);
				}
			}
			return new NumberList(results);
		} else if(node instanceof ConstantNode) {
			return resolveConstant((ConstantNode) node);
		} else {
			throw new CalculationException(CalculationException.REQUESTED_EMPTY_NODE);
		}
	}

	protected NumberList resolveConstant(ConstantNode node){
		NumberList resolved = constantsResolver.resolveConstant(node.getName());
		if(resolved == null)
			throw new CalculationException(CalculationException.UNKNOWN_CONSTANT, node.getName());
		return resolved;
	}

	protected BigDecimal resolveSuffix(SuffixOperatorNode node, BigDecimal operand){
		BigDecimal bigDecimal = suffixResolver.resolve(node.getOperator(), node.getCount(), operand);
		if(bigDecimal == null)
			throw new CalculationException(CalculationException.UNKNOWN_SUFFIX_OPERATOR);
		return bigDecimal;
	}

	protected NumberList processOperatorNode(OperatorNode node) {
		char symbol = node.getOperator();
		if (node.getFirstChild() == null || node.getSecondChild() == null)
			throw new CalculationException(CalculationException.INVALID_BINARY_OPERATOR);

		NumberList r1 = calc(node.getFirstChild());
		NumberList r2 = calc(node.getSecondChild());
		if (!r1.isSingleNumber() && !r2.isSingleNumber())
			throw new CalculationException(CalculationException.BINARY_OPERATOR_CANNOT_BE_APPLIED_TO_LISTS);

		TreeNode rightNode = node.getSecondChild();
		if (rightNode instanceof SuffixOperatorNode) {
			SuffixOperatorNode suffix = (SuffixOperatorNode) rightNode;
			if (suffix.getOperator().getSymbol().equals("%")) {
				if (r1.isSingleNumber()) { // 10-(25;50;100)%
					BigDecimal rb = r1.getSingleNumberIfTrue();
					return resolveList(r2, a -> resolver.calculatePercent(symbol, rb, a));
				} else { // (100;50)-50%
					BigDecimal rb = r2.getSingleNumberIfTrue();
					return resolveList(r1, a -> resolver.calculatePercent(symbol, a, rb));
				}
			}
		}

		if (r1.isSingleNumber()) {
			BigDecimal b = r1.getSingleNumberIfTrue();
			return resolveList(r2, a -> resolver.calculate(symbol, b, a));
		} else {
			BigDecimal b = r2.getSingleNumberIfTrue();
			return resolveList(r1, a -> resolver.calculate(symbol, a, b));
		}
	}

	protected NumberList processFunction(FunctionNode functionNode) {
		NumberList r = null;
		if (functionNode.getFirstChild() != null) {
			r = calc(functionNode.getFirstChild());
		}
		if (r == null) {
			return NumberList.of(resolveSingleArgumentList(functionNode, null));
		} else if (r.isSingleNumber()) {
			return NumberList.of(resolveSingleArgumentList(functionNode, r.getSingleNumberIfTrue()));
		}
		BigDecimal suffix = resolveFunctionSuffix(functionNode);
		NumberList resolved = listFunctionsResolver.resolve(functionNode.getFunctionName(), suffix, r);
		if(resolved == null)
			throw new CalculationException(CalculationException.UNKNOWN_FUNCTION);
		return resolved;
	}

	private BigDecimal resolveSingleArgumentList(FunctionNode functionNode, BigDecimal argument){
		BigDecimal suffix = resolveFunctionSuffix(functionNode);
		BigDecimal bigDecimal = functionsResolver.resolve(functionNode.getFunctionName(), suffix, argument);
		if(bigDecimal == null)
			throw new CalculationException(CalculationException.UNKNOWN_FUNCTION);
		return bigDecimal;
	}

	private BigDecimal resolveFunctionSuffix(FunctionNode functionNode){
		TreeNode suffixNode = functionNode.getSuffixNode();
		BigDecimal suffix = null;
		if(suffixNode != null){
			NumberList result = calc(suffixNode);
			if(!result.isSingleNumber())
				throw new CalculationException(CalculationException.SUFFIX_CANNOT_BE_LIST);
			suffix = result.getSingleNumberIfTrue();
		}
		return suffix;
	}

}
