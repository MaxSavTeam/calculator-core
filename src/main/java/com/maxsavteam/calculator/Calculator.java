/*
 * Copyright (C) 2021 MaxSav Team
 *
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
import com.maxsavteam.calculator.results.List;
import com.maxsavteam.calculator.results.Number;
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
import java.util.Locale;
import java.util.Map;

public class Calculator {

	public static final String PI_SIGN = "\u03C0";
	public static final String FI_SIGN = "\u03C6";
	public static final String E_SIGN = "\u0190";

	public static final String VERSION = "2.2.0";

	private final TreeBuilder builder;
	private final CalculatorExpressionTokenizer mExpressionTokenizer;
	private final CalculatorExpressionFormatter mBracketsChecker;
	public static final int roundScale = 8;
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
					return CalculatorUtils.removeZeros(a.divide(b, roundScale, RoundingMode.HALF_EVEN));
			}
			if (operator == '^')
				return CalculatorUtils.removeZeros(MathUtils.pow(a, b));
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
				return List.of(MathUtils.PI);
			case "fi":
			case FI_SIGN:
				return List.of(MathUtils.FI);
			case E_SIGN:
				return List.of(MathUtils.E);
			default:
				throw new CalculationException(CalculationException.UNKNOWN_CONSTANT, constantName);
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
			case "R":
			case "sqrt":
				if(suffix != null && operand != null){
					return MathUtils.rootWithBase(operand, suffix);
				}
				return MathUtils.rootWithBase(notNullNum, BigDecimal.valueOf(2));
			case "abs":
				return MathUtils.abs(notNullNum);
			case "rad":
			case "torad":
			case "to_rad":
				return MathUtils.toRadians(notNullNum);
			case "deg":
			case "todeg":
			case "to_deg":
				return MathUtils.toDegrees(notNullNum);
			default:
				return null;
		}
	};

	public static final ListFunctionsResolver defaultListFunctionResolver = (funcName, suffix, list) -> {
		List inlinedList = inlineElementsInList(list);
		BigDecimal[] decimals = inlinedList.getResults().stream()
				.filter(Number.class::isInstance)
				.map(b -> ((Number) b).get())
				.toArray(BigDecimal[]::new);

		switch (funcName) {
			case "A": {
				BigDecimal sum = BigDecimal.ZERO;
				for (var d : decimals)
					sum = sum.add(d);
				return List.of(sum.divide(BigDecimal.valueOf(decimals.length), roundScale, RoundingMode.HALF_EVEN));
			}
			case "gcd": {
				return List.of(MathUtils.gcd(decimals));
			}
			case "lcm": {
				return List.of(MathUtils.lcm(decimals));
			}
			case "sum": {
				BigDecimal sum = BigDecimal.ZERO;
				for (var d : decimals)
					sum = sum.add(d);
				return List.of(sum);
			}
			default: {
				return resolveList(list, b -> defaultFunctionsResolver.resolve(funcName, suffix, b));
			}
		}
	};

	private static List inlineElementsInList(List l) {
		ArrayList<BaseResult> results = new ArrayList<>();
		for (var b : l.getResults()) {
			if (b instanceof Number) {
				results.add(b);
			} else if (b instanceof List) {
				results.addAll(inlineElementsInList((List) b).getResults());
			}
		}
		return new List(results);
	}

	private interface ApplierForEachElement {
		BigDecimal apply(BigDecimal a);
	}

	private static List resolveList(List r, ApplierForEachElement applier) {
		if (r.isSingleNumber()) {
			return List.of(applier.apply(r.getSingleNumberIfTrue()));
		} else {
			ArrayList<BaseResult> resultsList = new ArrayList<>();
			for (var b : r.getResults()) {
				if (b instanceof Number) {
					resultsList.add(new Number(applier.apply(((Number) b).get())));
				} else if (b instanceof List) {
					resultsList.add(resolveList((List) b, applier));
				} else {
					resultsList.add(b);
				}
			}
			return new List(resultsList);
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
		if (operator == '!')
			return MathUtils.fact(operand, count);
		else if (operator == '%')
			return operand.divide(MathUtils.pow(BigDecimal.valueOf(100), BigDecimal.valueOf(count)), roundScale, RoundingMode.HALF_EVEN);
		throw new CalculationException(CalculationException.UNKNOWN_SUFFIX_OPERATOR);
	};

	public Calculator() {
		builder = new TreeBuilder();

		mBracketsChecker = new CalculatorExpressionFormatter();
		mBracketsChecker.setBracketsTypes(TreeBuilder.defaultBrackets);
		mBracketsChecker.setSuffixOperators(TreeBuilder.defaultSuffixOperators);

		mExpressionTokenizer = new CalculatorExpressionTokenizer();
		mExpressionTokenizer.setReplacementMap(defaultReplacementMap);
	}

	/**
	 * Sets brackets for TreeBuilder
	 **/
	public void setBracketsTypes(java.util.List<BracketsType> brackets) {
		builder.setBracketsTypes(brackets);
		mBracketsChecker.setBracketsTypes(brackets);
	}

	/**t
	 * Sets binary operators for TreeBuilder
	 **/
	public void setBinaryOperators(java.util.List<BinaryOperator> operators) {
		builder.setBinaryOperators(operators);
	}

	/**
	 * Sets suffix operators for TreeBuilder
	 **/
	public void setSuffixOperators(java.util.List<SuffixOperator> operators) {
		builder.setSuffixOperators(operators);
		mBracketsChecker.setSuffixOperators(operators);
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
		mExpressionTokenizer.setReplacementMap(map);
	}

	public void setDecimalSeparator(char decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}

	public void setGroupingSeparator(char groupingSeparator) {
		this.groupingSeparator = groupingSeparator;
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
		expr = mExpressionTokenizer.tokenizeExpression(expr);
		expr = mBracketsChecker.tryToCloseExpressionBrackets(expr);
		expr = mBracketsChecker.formatNearBrackets(expr);
		return expr;
	}

	/**
	 * Calculates answer of expression
	 */
	public List calculate(String expression) {
		java.util.List<TreeNode> nodes = builder.buildTree(formatExpression(expression));
		List r = calc(0, nodes);
		return formatAnswer(r);
	}

	private List formatAnswer(List r) {
		ArrayList<BaseResult> n = new ArrayList<>();
		for (var b : r.getResults()) {
			if (b instanceof List) {
				n.add(formatAnswer((List) b));
			} else if (b instanceof Number) {
				BigDecimal a = ((Number) b).get();
				a = CalculatorUtils.removeZeros(a);
				if (a.scale() > roundScale)
					a = a.setScale(roundScale, RoundingMode.HALF_EVEN);
				n.add(new Number(a));
			}
		}
		return new List(n);
	}

	private BigDecimal parseDecimal(String source) {
		try {
			return new BigDecimal(source);
		} catch (NumberFormatException e) {
			throw new CalculationException(CalculationException.NUMBER_FORMAT_EXCEPTION, e);
		}
	}

	private List calc(int v, java.util.List<TreeNode> nodes) {
		TreeNode node = nodes.get(v);

		if (node instanceof BracketsNode) {
			List r = calc(node.getLeftSonIndex(), nodes);
			int type = ((BracketsNode) node).getType();
			return resolveList(r, a -> bracketsResolver.resolve(type, a));
		} else if (node instanceof NumberNode) {
			return List.of(parseDecimal(((NumberNode) node).getNumber()));
		} else if (node instanceof NegativeNumberNode) {
			return NegativeNumberNode.apply(calc(node.getLeftSonIndex(), nodes));
		} else if (node instanceof FunctionNode) {
			return processFunction(v, nodes);
		} else if (node instanceof SuffixOperatorNode) {
			SuffixOperatorNode suffixNode = (SuffixOperatorNode) node;
			if (TreeBuilder.isNodeEmpty(node.getLeftSonIndex(), nodes))
				throw new CalculationException(CalculationException.NO_OPERAND_FOR_SUFFIX_OPERATOR);
			return resolveList(calc(node.getLeftSonIndex(), nodes), a -> resolveSuffix(suffixNode, a));
		} else if (node instanceof OperatorNode) {
			return processOperatorNode(v, nodes);
		} else if (node instanceof ListNode) {
			ListNode listNode = (ListNode) node;
			ArrayList<BaseResult> results = new ArrayList<>();
			for (TreeNode treeNode : listNode.getNodes()) {
				List r = calc(treeNode.getLeftSonIndex(), nodes);
				if (r.isSingleNumber()) {
					results.add(new Number(r.getSingleNumberIfTrue()));
				} else {
					results.add(r);
				}
			}
			return new List(results);
		} else if(node instanceof ConstantNode) {
			return resolveConstant((ConstantNode) node);
		} else {
			throw new CalculationException(CalculationException.REQUESTED_EMPTY_NODE);
		}
	}

	protected List resolveConstant(ConstantNode node){
		List resolved = constantsResolver.resolveConstant(node.getName());
		if(resolved == null)
			throw new CalculationException(CalculationException.UNKNOWN_CONSTANT);
		return resolved;
	}

	protected BigDecimal resolveSuffix(SuffixOperatorNode node, BigDecimal operand){
		BigDecimal bigDecimal = suffixResolver.resolve(node.operator, node.count, operand);
		if(bigDecimal == null)
			throw new CalculationException(CalculationException.UNKNOWN_SUFFIX_OPERATOR);
		return bigDecimal;
	}

	protected List processOperatorNode(int v, java.util.List<TreeNode> nodes) {
		TreeNode node = nodes.get(v);
		char symbol = ((OperatorNode) node).getOperator();
		if (TreeBuilder.isNodeEmpty(node.getLeftSonIndex(), nodes) || TreeBuilder.isNodeEmpty(node.getRightSonIndex(), nodes))
			throw new CalculationException(CalculationException.INVALID_BINARY_OPERATOR);

		List r1 = calc(node.getLeftSonIndex(), nodes);
		List r2 = calc(node.getRightSonIndex(), nodes);
		if (!r1.isSingleNumber() && !r2.isSingleNumber())
			throw new CalculationException(CalculationException.BINARY_OPERATOR_CANNOT_BE_APPLIED_TO_LISTS);

		TreeNode rightNode = nodes.get(node.getRightSonIndex());
		if (rightNode instanceof SuffixOperatorNode) {
			SuffixOperatorNode suffix = (SuffixOperatorNode) rightNode;
			if (suffix.operator == '%') {
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

	protected List processFunction(int v, java.util.List<TreeNode> nodes) {
		FunctionNode functionNode = (FunctionNode) nodes.get(v);
		List r = null;
		if (!TreeBuilder.isNodeEmpty(functionNode.getLeftSonIndex(), nodes)) {
			r = calc(functionNode.getLeftSonIndex(), nodes);
		}
		if (r == null) {
			return List.of(resolveSingleArgumentList(functionNode, null));
		} else if (r.isSingleNumber()) {
			return List.of(resolveSingleArgumentList(functionNode, r.getSingleNumberIfTrue()));
		}
		List resolved = listFunctionsResolver.resolve(functionNode.funcName, functionNode.suffix, r);
		if(resolved == null)
			throw new CalculationException(CalculationException.UNKNOWN_FUNCTION);
		return resolved;
	}

	private BigDecimal resolveSingleArgumentList(FunctionNode functionNode, BigDecimal argument){
		BigDecimal bigDecimal = functionsResolver.resolve(functionNode.funcName, functionNode.suffix, argument);
		if(bigDecimal == null)
			throw new CalculationException(CalculationException.UNKNOWN_FUNCTION);
		return bigDecimal;
	}

}
