package com.maxsavteam.calculator;

import com.maxsavteam.calculator.exceptions.CalculatingException;
import com.maxsavteam.calculator.tree.BracketsType;
import com.maxsavteam.calculator.tree.SuffixOperator;
import com.maxsavteam.calculator.tree.TreeBuilder;
import com.maxsavteam.calculator.utils.CalculatorUtils;

import java.util.ArrayList;
import java.util.Stack;

public class CalculatorExpressionFormatter {

    public static final Parameters defaultParameters = new Parameters.Builder().build();

    private ArrayList<BracketsType> bracketsTypes = TreeBuilder.defaultBrackets;
    private ArrayList<SuffixOperator> mSuffixOperators = TreeBuilder.defaultSuffixOperators;
    private final Parameters mParameters;

    public CalculatorExpressionFormatter() {
        this(defaultParameters);
    }

    public CalculatorExpressionFormatter(Parameters parameters) {
        mParameters = parameters;
    }

    public void setBracketsTypes(ArrayList<BracketsType> bracketsTypes) {
        this.bracketsTypes = bracketsTypes;
    }

    public void setSuffixOperators(ArrayList<SuffixOperator> suffixOperators) {
        mSuffixOperators = suffixOperators;
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
                        throw new CalculatingException(CalculatingException.INVALID_BRACKETS_SEQUENCE);
                    if (typesStack.peek() != type)
                        throw new CalculatingException(CalculatingException.INVALID_BRACKETS_SEQUENCE);
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
        StringBuilder sb = new StringBuilder();
        boolean isFunctionStarted = false;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            sb.append(c);
            if (CalculatorUtils.isLetter(c))
                isFunctionStarted = true;
            else if (!CalculatorUtils.isDigit(c) && c != '.')
                isFunctionStarted = false;
            if (i != expression.length() - 1) {
                char next = expression.charAt(i + 1);

                boolean isDigitBeforeOpenBracket = mParameters.isInsertMultiplySignBetweenNumberAndOpenBracket() &&
                        CalculatorUtils.isDigit(c) && findOpenBracket(next) != -1;

                boolean isDigitAfterCloseBracket = mParameters.isInsertMultiplySignBetweenNumberAndCloseBracket() &&
                        findCloseBracket(c) != -1 && CalculatorUtils.isDigit(next);

                boolean isSuffixOperatorBeforeDigit = mParameters.isInsertMultiplySignBetweenSuffixOperatorAndDigit() &&
                        isSuffixOperator(c) && CalculatorUtils.isDigit(next);

                boolean isSuffixOperatorBeforeOpenBracket = mParameters.isInsertMultiplySignBetweenSuffixOperatorAndOpenBracket() &&
                        isSuffixOperator(c) && findOpenBracket(next) != -1;

                boolean isSuffixOperatorBeforeFunction = mParameters.isInsertMultiplySignBetweenSuffixOperatorAndFunction() &&
                        isSuffixOperator(c) && CalculatorUtils.isLetter(next);

                boolean isFunctionSuffixBeforeOpenBracket = mParameters.isInsertMultiplySignBetweenFunctionSuffixAndOpenBracket() &&
                        CalculatorUtils.isDigit(c) && findOpenBracket(next) != -1 &&
                        isFunctionStarted;

                boolean isDigitBeforeFunction = mParameters.isInsertMultiplySignBetweenNumberAndFunction() &&
                        CalculatorUtils.isDigit(c) && CalculatorUtils.isLetter(next);

                boolean isCloseBracketBeforeOpen = mParameters.isInsertMultiplySignBetweenCloseAndOpenBrackets() &&
                        findCloseBracket(c) != -1 && findOpenBracket(next) != -1;

                if (
                        isDigitBeforeFunction ||
                                isFunctionSuffixBeforeOpenBracket ||
                                isCloseBracketBeforeOpen ||
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

    private boolean isSuffixOperator(char c) {
        for (SuffixOperator operator : mSuffixOperators)
            if (operator.symbol == c)
                return true;
        return false;
    }

    public static class Parameters {
        private final boolean insertMultiplySignBetweenNumberAndFunction;
        private final boolean insertMultiplySignBetweenNumberAndOpenBracket;
        private final boolean insertMultiplySignBetweenNumberAndCloseBracket;
        private final boolean insertMultiplySignBetweenSuffixOperatorAndDigit;
        private final boolean insertMultiplySignBetweenSuffixOperatorAndFunction;
        private final boolean insertMultiplySignBetweenSuffixOperatorAndOpenBracket;
        private final boolean insertMultiplySignBetweenFunctionSuffixAndOpenBracket;
        private final boolean insertMultiplySignBetweenCloseAndOpenBrackets;

        public Parameters(boolean insertMultiplySignBetweenNumberAndFunction,
                          boolean insertMultiplySignBetweenNumberAndOpenBracket,
                          boolean insertMultiplySignBetweenNumberAndCloseBracket,
                          boolean insertMultiplySignBetweenSuffixOperatorAndDigit,
                          boolean insertMultiplySignBetweenSuffixOperatorAndFunction,
                          boolean insertMultiplySignBetweenSuffixOperatorAndOpenBracket,
                          boolean insertMultiplySignBetweenFunctionSuffixAndOpenBracket,
                          boolean insertMultiplySignBetweenCloseAndOpenBrackets) {
            this.insertMultiplySignBetweenNumberAndFunction = insertMultiplySignBetweenNumberAndFunction;
            this.insertMultiplySignBetweenNumberAndOpenBracket = insertMultiplySignBetweenNumberAndOpenBracket;
            this.insertMultiplySignBetweenNumberAndCloseBracket = insertMultiplySignBetweenNumberAndCloseBracket;
            this.insertMultiplySignBetweenSuffixOperatorAndDigit = insertMultiplySignBetweenSuffixOperatorAndDigit;
            this.insertMultiplySignBetweenSuffixOperatorAndFunction = insertMultiplySignBetweenSuffixOperatorAndFunction;
            this.insertMultiplySignBetweenSuffixOperatorAndOpenBracket = insertMultiplySignBetweenSuffixOperatorAndOpenBracket;
            this.insertMultiplySignBetweenFunctionSuffixAndOpenBracket = insertMultiplySignBetweenFunctionSuffixAndOpenBracket;
            this.insertMultiplySignBetweenCloseAndOpenBrackets = insertMultiplySignBetweenCloseAndOpenBrackets;
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

        public static class Builder {
            private boolean insertMultiplySignBetweenNumberAndFunction = true;
            private boolean insertMultiplySignBetweenNumberAndOpenBracket = true;
            private boolean insertMultiplySignBetweenNumberAndCloseBracket = true;
            private boolean insertMultiplySignBetweenSuffixOperatorAndDigit = true;
            private boolean insertMultiplySignBetweenSuffixOperatorAndFunction = true;
            private boolean insertMultiplySignBetweenSuffixOperatorAndOpenBracket = true;
            private boolean insertMultiplySignBetweenFunctionSuffixAndOpenBracket = false;
            private boolean insertMultiplySignBetweenCloseAndOpenBrackets = true;

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

            public Parameters build() {
                return new Parameters(
                        insertMultiplySignBetweenNumberAndFunction,
                        insertMultiplySignBetweenNumberAndOpenBracket,
                        insertMultiplySignBetweenNumberAndCloseBracket,
                        insertMultiplySignBetweenSuffixOperatorAndDigit,
                        insertMultiplySignBetweenSuffixOperatorAndFunction,
                        insertMultiplySignBetweenSuffixOperatorAndOpenBracket,
                        insertMultiplySignBetweenFunctionSuffixAndOpenBracket,
                        insertMultiplySignBetweenCloseAndOpenBrackets
                );
            }

        }

    }

}
