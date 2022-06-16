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

package com.maxsavteam.calculator.tree;

import com.maxsavteam.calculator.Calculator;
import com.maxsavteam.calculator.exceptions.CalculationException;
import com.maxsavteam.calculator.exceptions.TreeBuildingException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Builds tree from expression.<br>
 * <p>
 * Builder returns root node of tree.
 * If node is not a {@link NumberNode}, it will have at least one child ({@link TreeNode#getFirstChild()}).<br>
 * Child can be obtained by calling {@link TreeNode#getFirstChild()}.
 * {@link OperatorNode} has two children, and they can be obtained by calling {@link OperatorNode#getFirstChild()} and {@link OperatorNode#getSecondChild()}
 * </p>
 * Parser based on numbers, operators, functions and constants. Operator can be binary (+, -, *, /, ^ (power)),<br>
 * bracket or suffix (this operators follow after number (e.g. % (percent), ! (factorial)).<br>
 * All of these operators you can specify.
 *
 * <p>
 * <h3>Binary operators</h3>
 * All binary operators have two sons - left operand (first child) and right operand (second child).<br>
 * But if - operator has nothing before it (or bracket), then {@link OperatorNode} will be replaced with {@link NegativeNumberNode}.
 * Also, if + operator has only right son, then plus sign will be skipped.
 * </p>
 *
 * <p>
 * <h3>Brackets operators</h3>
 * {@link BracketsNode} contain type of bracket<br>
 * </p>
 *
 * <p>
 * <h3>Suffix operators</h3>
 * {@link SuffixOperatorNode} contain symbol of operator and count and will be applied to its child<br>
 * </p>
 *
 * <p>
 * <h3>Numbers</h3>
 * {@link NumberNode} is a leaf of tree and does not have any son.<br>
 * </p>
 *
 * <p>
 * <h3>Functions</h3>
 * {@link FunctionNode} contain name of function. All letters will be parsed as functions.
 * Function also has suffix. It is number straight after name of function
 * (e.g. "sin45", sinus will be there as function name and 45 as suffix).
 * Suffix can have its own node ({@link FunctionNode#getSuffixNode()})<br>
 * If function name and suffix followed by some type of bracket, then {@link FunctionNode}'s will be
 * {@link BracketsNode} with all rules described above.
 * </p>
 *
 * <p>
 * <h3>Constants</h3>
 * A constant is a sequence of letters followed by any operator or symbol that is not a digit (otherwise it is a function with a suffix)
 * or a bracket (a regular function)<br>
 * For example, in expression "sin(pi/6)" "sin" is a function (because it is followed by a bracket), "pi" is a constant
 * {@link ConstantNode} contains name of constant.
 * </p>
 */
public class TreeBuilder {

	public static final List<BracketsType> defaultBrackets = List.of(
			new BracketsType('(', ')', 1),
			new BracketsType('[', ']', 2), // round
			new BracketsType('\u23A3', '\u23A6', 3), // round floor
			new BracketsType('\u23A1', '\u23A4', 4) // round ceil
	);
	public static final List<BinaryOperator> defaultBinaryOperators = List.of(
			new BinaryOperator('+', 0),
			new BinaryOperator('-', 0),
			new BinaryOperator('*', 1),
			new BinaryOperator('/', 1),
			new BinaryOperator('^', 2)
	);
	public static final List<SuffixOperator> defaultSuffixOperators = List.of(
			new SuffixOperator('!'),
			new SuffixOperator('%'),
			new SuffixOperator(Calculator.DEGREE_SIGN),
			new SuffixOperator(Calculator.GRAD_SIGN)
	);

	private List<BracketsType> brackets = defaultBrackets;
	private List<BinaryOperator> operators = defaultBinaryOperators;
	private List<SuffixOperator> suffixOperators = defaultSuffixOperators;
	private ArrayList<OperatorPosition> mOperatorPositions;
	private ArrayList<SemicolonPosition> mSemicolonPositions;

	/**
	 * Sets custom brackets
	 */
	public void setBracketsTypes(List<BracketsType> brackets) {
		this.brackets = brackets;
	}

	/**
	 * Sets custom binary operators
	 */
	public void setBinaryOperators(List<BinaryOperator> operators) {
		CalculatorUtils.requireUniqueItems(operators);
		this.operators = operators;
	}

	/**
	 * Sets custom suffix operators
	 */
	public void setSuffixOperators(List<SuffixOperator> suffixOperators) {
		CalculatorUtils.requireUniqueItems(suffixOperators);
		this.suffixOperators = suffixOperators;
	}

	public List<BracketsType> getBrackets() {
		return new ArrayList<>(brackets);
	}

	public List<BinaryOperator> getOperators() {
		return new ArrayList<>(operators);
	}

	public List<SuffixOperator> getSuffixOperators() {
		return new ArrayList<>(suffixOperators);
	}

	/**
	 * Builds tree from expression.<br>
	 * Parses this expression and resolves binary operators and semicolons.
	 */
	public TreeNode buildTree(String expression) {
		mOperatorPositions = new ArrayList<>();
		mSemicolonPositions = new ArrayList<>();

		int bracketsLevel = 0;
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if (isOpenBracket(c))
				bracketsLevel++;
			else if (isCloseBracket(c))
				bracketsLevel--;
			else if (c == ';') {
				mSemicolonPositions.add(new SemicolonPosition(bracketsLevel, i));
			} else if (isBinaryOperator(c)) {
				int priority = getOperatorPriority(c);
				mOperatorPositions.add(new OperatorPosition(bracketsLevel, priority, i));
			}
		}

		return build(expression, 0, 0);
	}

	/**
	 * @param expression    Current part of original expression
	 * @param rootLevel     Current level of brackets.
	 *                      Necessary, because expression is cut, but in operatorPositions
	 *                      old positions of this operators
	 * @param exampleOffset Index of this expression in original expression.
	 *                      Necessary, because expression is cut, but in operatorPositions
	 *                      old positions of this operators
	 */
	protected TreeNode build(String expression, int rootLevel, int exampleOffset) {
		if (expression.length() == 0)
			return null;
		int minLevel = getBracketsMinLevel(expression);

		if (minLevel >= 1) {
			int startBracketType = getBracketType(expression.charAt(0));
			TreeNode child = build(expression.substring(1, expression.length() - 1), rootLevel + 1, exampleOffset + 1);
			BracketsNode node = new BracketsNode(startBracketType);
			node.setFirstChild(child);
			return node;
		}

		List<SemicolonPosition> semicolonPositions = findSemicolonsInExpression(expression, exampleOffset, rootLevel);
		if (semicolonPositions.size() > 0) {
			return parseList(expression, rootLevel, exampleOffset, semicolonPositions);
		}

		OperatorPosition nearestOperatorInExpression = findNearestOperatorInExpression(expression, exampleOffset, rootLevel);
		if (nearestOperatorInExpression != null) {
			return parseBinaryOperator(expression, exampleOffset, rootLevel, nearestOperatorInExpression);
		}

		if (CalculatorUtils.isLetter(expression.charAt(0))) {
			return parseFunc(expression, exampleOffset, rootLevel);
		}
		String last = expression.substring(expression.length() - 1);
		if (isSuffixOperator(last)) {
			return parseSuffixOperator(expression, exampleOffset, rootLevel);
		}
		return new NumberNode(expression);
	}

	protected ListNode parseList(String expression, int rootLevel, int exampleOffset, List<SemicolonPosition> semicolonPositions) {
		semicolonPositions.add(0, new SemicolonPosition(0, exampleOffset - 1));
		semicolonPositions.add(new SemicolonPosition(0, expression.length() + exampleOffset));
		ArrayList<String> parts = new ArrayList<>();
		for (int i = 0; i < semicolonPositions.size() - 1; i++) {
			SemicolonPosition pos = semicolonPositions.get(i);
			SemicolonPosition next = semicolonPositions.get(i + 1);
			parts.add(expression.substring(pos.position + 1 - exampleOffset, next.position - exampleOffset));
		}
		ArrayList<TreeNode> nodes = new ArrayList<>();
		int partsOffset = 0;
		for (String part : parts) {
			if (!part.isEmpty()) {
				TreeNode treeNode = build(part, rootLevel, exampleOffset + partsOffset);
				nodes.add(treeNode);
			}
			partsOffset += part.length() + 1;
		}
		return new ListNode(nodes);
	}

	protected TreeNode parseBinaryOperator(String expression, int exampleOffset, int rootLevel, OperatorPosition operatorPosition) {
		String firstPart = expression.substring(0, operatorPosition.position - exampleOffset);
		String secondPart = expression.substring(operatorPosition.position - exampleOffset + 1);

		char operator = expression.charAt(operatorPosition.position - exampleOffset);

		if (firstPart.isEmpty() && !secondPart.isEmpty()) {
			if (operator == '+') {
				return build(secondPart, rootLevel, operatorPosition.position + 1);
			} else if (operator == '-') {
				NegativeNumberNode negativeNumberNode = new NegativeNumberNode();
				negativeNumberNode.setFirstChild(build(secondPart, rootLevel, operatorPosition.position + 1));
				return negativeNumberNode;
			} else {
				return null;
			}
		}
		OperatorNode node = new OperatorNode(operator);
		node.setFirstChild(build(firstPart, rootLevel, exampleOffset));
		node.setSecondChild(build(secondPart, rootLevel, operatorPosition.position + 1));
		return node;
	}

	protected SuffixOperatorNode parseSuffixOperator(String ex, int offset, int rootLevel) {
		String operator = ex.substring(ex.length() - 1);
		int i = ex.length() - 1;
		int count = 1;
		while (i >= 1 && ex.substring(i - 1, i).equals(operator)) {
			count++;
			i--;
		}
		SuffixOperator suffixOperator = suffixOperators
				.stream()
				.filter(o -> o.getSymbol().equals(operator))
				.findAny()
				.orElseThrow();
		SuffixOperatorNode node = new SuffixOperatorNode(suffixOperator, count);
		node.setFirstChild(build(ex.substring(0, i), rootLevel, offset));
		return node;
	}

	protected TreeNode parseFunc(String ex, int offset, int rootLevel) {
		StringBuilder funcName = new StringBuilder();
		int i = 0;
		while (i < ex.length() && CalculatorUtils.isLetter(ex.charAt(i))) {
			funcName.append(ex.charAt(i));
			i++;
		}
		if (i == ex.length() || !CalculatorUtils.isDigit(ex.charAt(i))) {
			if (i != ex.length() && !CalculatorUtils.isDigit(ex.charAt(i))) {
				FunctionNode node = new FunctionNode(funcName.toString(), null);
				node.setFirstChild(build(ex.substring(i), rootLevel, offset + i));
				return node;
			}
			return new ConstantNode(funcName.toString());
		}
		// sin2! should be recognized as sin(2!), sin30° => sin(30°), that is why suffixes should be evaluated
		int suffixStartIndex = i;
		while (i < ex.length()) {
			String character = ex.substring(i, i + 1);
			if (!CalculatorUtils.isDigit(ex.charAt(i)) && ex.charAt(i) != '.' && !isSuffixOperator(character)) {
				break;
			}
			i++;
		}
		TreeNode suffixNode = build(ex.substring(suffixStartIndex, i), rootLevel, offset + suffixStartIndex);
		FunctionNode node = new FunctionNode(funcName.toString(), suffixNode);
		if (i < ex.length()) {
			node.setFirstChild(build(ex.substring(i), rootLevel, offset + i));
		}
		return node;
	}

	protected OperatorPosition findNearestOperatorInExpression(String ex, int offset, int rootLevel) {
		int end = offset + ex.length();
		OperatorPosition foundPos = null;
		for (OperatorPosition pos : mOperatorPositions) {
			if (offset > pos.position)
				continue;
			if (pos.position >= end) {
				break;
			}
			if (pos.level != rootLevel)
				continue;

			if (foundPos == null || pos.priority <= foundPos.priority)
				foundPos = pos;
		}
		return foundPos;
	}

	protected List<SemicolonPosition> findSemicolonsInExpression(String ex, int offset, int level) {
		int end = offset + ex.length();
		List<SemicolonPosition> positions = new ArrayList<>();
		for (SemicolonPosition semicolonPosition : mSemicolonPositions) {
			if (offset > semicolonPosition.position || semicolonPosition.level != level)
				continue;
			if (semicolonPosition.position >= end)
				break;
			positions.add(semicolonPosition);
		}
		return positions;
	}

	protected int getBracketsMinLevel(String ex) {
		Stack<Integer> typesStack = new Stack<>();
		int minLevel = Integer.MAX_VALUE;

		for (int i = 0; i < ex.length(); i++) {
			char c = ex.charAt(i);
			if (isOpenBracket(c)) {
				typesStack.push(getBracketType(c));
			} else if (isCloseBracket(c)) {
				if (typesStack.isEmpty() || typesStack.peek() != getBracketType(c))
					throw new TreeBuildingException(CalculationException.INVALID_BRACKETS_SEQUENCE);
				typesStack.pop();
			} else {
				minLevel = Math.min(minLevel, typesStack.size());
			}
		}
		return minLevel;
	}

	private boolean isOpenBracket(char c) {
		for (BracketsType bracketsType : brackets) {
			if (bracketsType.openBracket == c)
				return true;
		}
		return false;
	}

	private boolean isCloseBracket(char c) {
		for (BracketsType bracketsType : brackets) {
			if (bracketsType.closeBracket == c)
				return true;
		}
		return false;
	}

	private boolean isSuffixOperator(String s) {
		for (SuffixOperator operator : suffixOperators) {
			if (operator.getSymbol().equals(s))
				return true;
		}
		return false;
	}

	private int getBracketType(char c) {
		for (BracketsType bracketsType : brackets) {
			if (bracketsType.openBracket == c || bracketsType.closeBracket == c)
				return bracketsType.type;
		}
		return -1;
	}

	private boolean isBinaryOperator(char c) {
		for (BinaryOperator operator : operators) {
			if (operator.symbol == c)
				return true;
		}
		return false;
	}

	private int getOperatorPriority(char c) {
		for (BinaryOperator operator : operators)
			if (operator.symbol == c)
				return operator.priority;
		return Integer.MAX_VALUE;
	}

}
