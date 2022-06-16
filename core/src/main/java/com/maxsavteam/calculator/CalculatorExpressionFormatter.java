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
import com.maxsavteam.calculator.tree.BracketsType;
import com.maxsavteam.calculator.tree.SuffixOperator;
import com.maxsavteam.calculator.tree.TreeBuilder;
import com.maxsavteam.calculator.utils.CalculatorUtils;

import java.util.List;
import java.util.Stack;

public class CalculatorExpressionFormatter {

	public static final Parameters defaultParameters = new Parameters.Builder().build();

	private List<BracketsType> bracketsTypes = TreeBuilder.defaultBrackets;
	private List<SuffixOperator> suffixOperators = TreeBuilder.defaultSuffixOperators;
	private final Parameters parameters;

	public CalculatorExpressionFormatter() {
		this(defaultParameters);
	}

	public CalculatorExpressionFormatter(Parameters parameters) {
		this.parameters = parameters;
	}

	public void setBracketsTypes(List<BracketsType> bracketsTypes) {
		this.bracketsTypes = bracketsTypes;
	}

	public void setSuffixOperators(List<SuffixOperator> suffixOperators) {
		this.suffixOperators = suffixOperators;
	}

	public String removeSpaces(String expression){
		return expression.replace(" ", "");
	}

	public String tryToCloseExpressionBrackets(String expression) {
		StringBuilder sbResult = new StringBuilder(expression);

		Stack<Integer> typesStack = new Stack<>();

		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			int type = findOpenBracket(c);
			boolean isOpenBracket = type != -1;
			if (isOpenBracket) {
				typesStack.push(type);
			} else {
				type = findCloseBracket(c);
				if (type != -1) {
					if (typesStack.isEmpty())
						throw new CalculationException(CalculationException.INVALID_BRACKETS_SEQUENCE);
					if (typesStack.peek() != type)
						throw new CalculationException(CalculationException.INVALID_BRACKETS_SEQUENCE);
					else
						typesStack.pop();
				}
			}
		}
		while (!typesStack.isEmpty()) {
			int type = typesStack.pop();
			for (BracketsType bracketsType : bracketsTypes) {
				if (bracketsType.type == type)
					sbResult.append(bracketsType.closeBracket);
			}
		}
		return sbResult.toString();
	}

	public String formatNearBrackets(String expression) {
		return normalizeExpression(expression);
	}

	public String normalizeExpression(String expression) {
		StringBuilder sb = new StringBuilder();
		boolean isFunctionStarted = false;
		for (int i = 0; i < expression.length(); i++) {
			String current = expression.substring(i, i + 1);
			char c = current.charAt(0);
			sb.append(c);
			if (CalculatorUtils.isLetter(c))
				isFunctionStarted = true;
			else if (!CalculatorUtils.isDigit(c) && c != '.')
				isFunctionStarted = false;
			if (i != expression.length() - 1) {
				char next = expression.charAt(i + 1);

				boolean isNowDigit = CalculatorUtils.isDigit(c);
				boolean isNextDigit = CalculatorUtils.isDigit(next);
				boolean isNextOpenBracket = findOpenBracket(next) != -1;
				boolean isNowCloseBracket = findCloseBracket(c) != -1;
				boolean isNowSuffixOperator = isSuffixOperator(current);
				boolean isNextLetter = CalculatorUtils.isLetter(next);

				boolean isDigitBeforeOpenBracket = parameters.isInsertMultiplySignBetweenNumberAndOpenBracket() &&
						isNowDigit && isNextOpenBracket;

				boolean isDigitAfterCloseBracket = parameters.isInsertMultiplySignBetweenNumberAndCloseBracket() &&
						isNowCloseBracket && isNextDigit;

				boolean isFunctionAfterCloseBracket = parameters.isInsertMultiplySignBetweenCloseBracketAndFunctionOrConstant()
						&& isNowCloseBracket && isNextLetter;

				boolean isSuffixOperatorBeforeDigit = parameters.isInsertMultiplySignBetweenSuffixOperatorAndDigit() &&
						isNowSuffixOperator && isNextDigit;

				boolean isSuffixOperatorBeforeOpenBracket = parameters.isInsertMultiplySignBetweenSuffixOperatorAndOpenBracket() &&
						isNowSuffixOperator && isNextOpenBracket;

				boolean isSuffixOperatorBeforeFunction = parameters.isInsertMultiplySignBetweenSuffixOperatorAndFunction() &&
						isNowSuffixOperator && isNextLetter;

				boolean isFunctionSuffixBeforeOpenBracket = parameters.isInsertMultiplySignBetweenFunctionSuffixAndOpenBracket() &&
						isNowDigit && isNextOpenBracket &&
						isFunctionStarted;

				boolean isDigitBeforeFunction = parameters.isInsertMultiplySignBetweenNumberAndFunction() &&
						isNowDigit && isNextLetter;

				boolean isCloseBracketBeforeOpen = parameters.isInsertMultiplySignBetweenCloseAndOpenBrackets() &&
						isNowCloseBracket && isNextOpenBracket;

				boolean isCloseBracketBeforeDot = parameters.isInsertZeroBetweenCloseBracketAndDot() &&
						isNowCloseBracket && next == '.';

				boolean isNonDigitBeforeDot = parameters.isInsertZeroBetweenNonDigitAndDot() &&
						!isNowDigit && next == '.';

				if (isCloseBracketBeforeDot)
					sb.append("*0");
				else if (isNonDigitBeforeDot)
					sb.append("0");

				if (
						isDigitBeforeFunction ||
								isFunctionSuffixBeforeOpenBracket ||
								isCloseBracketBeforeOpen ||
								isFunctionAfterCloseBracket ||
								!isFunctionStarted && (
										isDigitBeforeOpenBracket ||
												isDigitAfterCloseBracket ||
												isSuffixOperatorBeforeDigit ||
												isSuffixOperatorBeforeOpenBracket ||
												isSuffixOperatorBeforeFunction
								)
				) {
					sb.append('*');
				}
			}
		}
		return sb.toString();
	}

	private int findOpenBracket(char c) {
		for (BracketsType type : bracketsTypes) {
			if (c == type.openBracket)
				return type.type;
		}
		return -1;
	}

	private int findCloseBracket(char c) {
		for (BracketsType type : bracketsTypes) {
			if (c == type.closeBracket)
				return type.type;
		}
		return -1;
	}

	private boolean isSuffixOperator(String s) {
		for (SuffixOperator operator : suffixOperators)
			if (operator.getSymbol().equals(s))
				return true;
		return false;
	}

	public static class Parameters {
		private final boolean insertMultiplySignBetweenNumberAndFunction;
		private final boolean insertMultiplySignBetweenNumberAndOpenBracket;
		private final boolean insertMultiplySignBetweenNumberAndCloseBracket;
		private final boolean insertMultiplySignBetweenCloseBracketAndFunctionOrConstant;
		private final boolean insertMultiplySignBetweenSuffixOperatorAndDigit;
		private final boolean insertMultiplySignBetweenSuffixOperatorAndFunction;
		private final boolean insertMultiplySignBetweenSuffixOperatorAndOpenBracket;
		private final boolean insertMultiplySignBetweenFunctionSuffixAndOpenBracket;
		private final boolean insertMultiplySignBetweenCloseAndOpenBrackets;

		private final boolean insertZeroBetweenCloseBracketAndDot;
		private final boolean insertZeroBetweenNonDigitAndDot;

		private Parameters(boolean insertMultiplySignBetweenNumberAndFunction,
		                   boolean insertMultiplySignBetweenNumberAndOpenBracket,
		                   boolean insertMultiplySignBetweenNumberAndCloseBracket,
						   boolean insertMultiplySignBetweenCloseBracketAndFunctionOrConstant,
		                   boolean insertMultiplySignBetweenSuffixOperatorAndDigit,
		                   boolean insertMultiplySignBetweenSuffixOperatorAndFunction,
		                   boolean insertMultiplySignBetweenSuffixOperatorAndOpenBracket,
		                   boolean insertMultiplySignBetweenFunctionSuffixAndOpenBracket,
		                   boolean insertMultiplySignBetweenCloseAndOpenBrackets,
		                   boolean insertZeroBetweenCloseBracketAndDot,
		                   boolean insertZeroBetweenNonDigitAndDot) {
			this.insertMultiplySignBetweenNumberAndFunction = insertMultiplySignBetweenNumberAndFunction;
			this.insertMultiplySignBetweenNumberAndOpenBracket = insertMultiplySignBetweenNumberAndOpenBracket;
			this.insertMultiplySignBetweenNumberAndCloseBracket = insertMultiplySignBetweenNumberAndCloseBracket;
			this.insertMultiplySignBetweenCloseBracketAndFunctionOrConstant = insertMultiplySignBetweenCloseBracketAndFunctionOrConstant;
			this.insertMultiplySignBetweenSuffixOperatorAndDigit = insertMultiplySignBetweenSuffixOperatorAndDigit;
			this.insertMultiplySignBetweenSuffixOperatorAndFunction = insertMultiplySignBetweenSuffixOperatorAndFunction;
			this.insertMultiplySignBetweenSuffixOperatorAndOpenBracket = insertMultiplySignBetweenSuffixOperatorAndOpenBracket;
			this.insertMultiplySignBetweenFunctionSuffixAndOpenBracket = insertMultiplySignBetweenFunctionSuffixAndOpenBracket;
			this.insertMultiplySignBetweenCloseAndOpenBrackets = insertMultiplySignBetweenCloseAndOpenBrackets;
			this.insertZeroBetweenCloseBracketAndDot = insertZeroBetweenCloseBracketAndDot;
			this.insertZeroBetweenNonDigitAndDot = insertZeroBetweenNonDigitAndDot;
		}

		public boolean isInsertMultiplySignBetweenNumberAndFunction() {
			return insertMultiplySignBetweenNumberAndFunction;
		}

		public boolean isInsertMultiplySignBetweenNumberAndOpenBracket() {
			return insertMultiplySignBetweenNumberAndOpenBracket;
		}

		public boolean isInsertMultiplySignBetweenNumberAndCloseBracket() {
			return insertMultiplySignBetweenNumberAndCloseBracket;
		}

		public boolean isInsertMultiplySignBetweenCloseBracketAndFunctionOrConstant() {
			return insertMultiplySignBetweenCloseBracketAndFunctionOrConstant;
		}

		public boolean isInsertMultiplySignBetweenSuffixOperatorAndDigit() {
			return insertMultiplySignBetweenSuffixOperatorAndDigit;
		}

		public boolean isInsertMultiplySignBetweenSuffixOperatorAndFunction() {
			return insertMultiplySignBetweenSuffixOperatorAndFunction;
		}

		public boolean isInsertMultiplySignBetweenSuffixOperatorAndOpenBracket() {
			return insertMultiplySignBetweenSuffixOperatorAndOpenBracket;
		}

		public boolean isInsertMultiplySignBetweenFunctionSuffixAndOpenBracket() {
			return insertMultiplySignBetweenFunctionSuffixAndOpenBracket;
		}

		public boolean isInsertMultiplySignBetweenCloseAndOpenBrackets() {
			return insertMultiplySignBetweenCloseAndOpenBrackets;
		}

		public boolean isInsertZeroBetweenCloseBracketAndDot() {
			return insertZeroBetweenCloseBracketAndDot;
		}

		public boolean isInsertZeroBetweenNonDigitAndDot() {
			return insertZeroBetweenNonDigitAndDot;
		}

		public static class Builder {
			private boolean insertMultiplySignBetweenNumberAndFunction = true;
			private boolean insertMultiplySignBetweenNumberAndOpenBracket = true;
			private boolean insertMultiplySignBetweenNumberAndCloseBracket = true;
			private boolean insertMultiplySignBetweenCloseBracketAndFunctionOrConstant = true;
			private boolean insertMultiplySignBetweenSuffixOperatorAndDigit = true;
			private boolean insertMultiplySignBetweenSuffixOperatorAndFunction = true;
			private boolean insertMultiplySignBetweenSuffixOperatorAndOpenBracket = true;
			private boolean insertMultiplySignBetweenFunctionSuffixAndOpenBracket = false;
			private boolean insertMultiplySignBetweenCloseAndOpenBrackets = true;
			private boolean insertZeroBetweenCloseBracketAndDot = true;
			private boolean insertZeroBetweenNonDigitAndDot = true;

			public Builder setInsertMultiplySignBetweenNumberAndFunction(boolean insertMultiplySignBetweenNumberAndFunction) {
				this.insertMultiplySignBetweenNumberAndFunction = insertMultiplySignBetweenNumberAndFunction;
				return this;
			}

			public Builder setInsertMultiplySignBetweenNumberAndOpenBracket(boolean insertMultiplySignBetweenNumberAndOpenBracket) {
				this.insertMultiplySignBetweenNumberAndOpenBracket = insertMultiplySignBetweenNumberAndOpenBracket;
				return this;
			}

			public Builder setInsertMultiplySignBetweenNumberAndCloseBracket(boolean insertMultiplySignBetweenNumberAndCloseBracket) {
				this.insertMultiplySignBetweenNumberAndCloseBracket = insertMultiplySignBetweenNumberAndCloseBracket;
				return this;
			}

			public Builder setInsertMultiplySignBetweenCloseBracketAndFunctionOrConstant(boolean insertMultiplySignBetweenCloseBracketAndFunctionOrConstant) {
				this.insertMultiplySignBetweenCloseBracketAndFunctionOrConstant = insertMultiplySignBetweenCloseBracketAndFunctionOrConstant;
				return this;
			}

			public Builder setInsertMultiplySignBetweenSuffixOperatorAndDigit(boolean insertMultiplySignBetweenSuffixOperatorAndDigit) {
				this.insertMultiplySignBetweenSuffixOperatorAndDigit = insertMultiplySignBetweenSuffixOperatorAndDigit;
				return this;
			}

			public Builder setInsertMultiplySignBetweenSuffixOperatorAndFunction(boolean insertMultiplySignBetweenSuffixOperatorAndFunction) {
				this.insertMultiplySignBetweenSuffixOperatorAndFunction = insertMultiplySignBetweenSuffixOperatorAndFunction;
				return this;
			}

			public Builder setInsertMultiplySignBetweenSuffixOperatorAndOpenBracket(boolean insertMultiplySignBetweenSuffixOperatorAndOpenBracket) {
				this.insertMultiplySignBetweenSuffixOperatorAndOpenBracket = insertMultiplySignBetweenSuffixOperatorAndOpenBracket;
				return this;
			}

			public Builder setInsertMultiplySignBetweenFunctionSuffixAndOpenBracket(boolean insertMultiplySignBetweenFunctionSuffixAndOpenBracket) {
				this.insertMultiplySignBetweenFunctionSuffixAndOpenBracket = insertMultiplySignBetweenFunctionSuffixAndOpenBracket;
				return this;
			}

			public Builder setInsertMultiplySignBetweenCloseAndOpenBrackets(boolean insertMultiplySignBetweenCloseAndOpenBrackets) {
				this.insertMultiplySignBetweenCloseAndOpenBrackets = insertMultiplySignBetweenCloseAndOpenBrackets;
				return this;
			}

			public Builder setInsertZeroBetweenCloseBracketAndDot(boolean insertZeroBetweenCloseBracketAndDot) {
				this.insertZeroBetweenCloseBracketAndDot = insertZeroBetweenCloseBracketAndDot;
				return this;
			}

			public Builder setInsertZeroBetweenNonDigitAndDot(boolean insertZeroBetweenNonDigitAndDot) {
				this.insertZeroBetweenNonDigitAndDot = insertZeroBetweenNonDigitAndDot;
				return this;
			}

			public Parameters build() {
				return new Parameters(
						insertMultiplySignBetweenNumberAndFunction,
						insertMultiplySignBetweenNumberAndOpenBracket,
						insertMultiplySignBetweenNumberAndCloseBracket,
						insertMultiplySignBetweenCloseBracketAndFunctionOrConstant,
						insertMultiplySignBetweenSuffixOperatorAndDigit,
						insertMultiplySignBetweenSuffixOperatorAndFunction,
						insertMultiplySignBetweenSuffixOperatorAndOpenBracket,
						insertMultiplySignBetweenFunctionSuffixAndOpenBracket,
						insertMultiplySignBetweenCloseAndOpenBrackets,
						insertZeroBetweenCloseBracketAndDot,
						insertZeroBetweenNonDigitAndDot
				);
			}

		}

	}

}
