package com.maxsavteam.calculator;

import com.maxsavteam.calculator.exceptions.CalculatingException;
import com.maxsavteam.calculator.tree.BracketsType;
import com.maxsavteam.calculator.tree.SuffixOperator;
import com.maxsavteam.calculator.utils.CalculatorUtils;

import java.util.ArrayList;
import java.util.Stack;

public class CalculatorExpressionFormatter {

    private ArrayList<BracketsType> bracketsTypes = new ArrayList<>();
    private ArrayList<SuffixOperator> mSuffixOperators = new ArrayList<>();

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
            if (!isFunctionStarted && i != expression.length() - 1) {
                char next = expression.charAt(i + 1);
                boolean isDigitBeforeOpenBracket = CalculatorUtils.isDigit(c) && findOpenBracket(next) != -1;
                boolean isDigitAfterCloseBracket = findCloseBracket(c) != -1 && CalculatorUtils.isDigit(next);
                boolean isSuffixOperatorBeforeDigit = isSuffixOperator(c) && CalculatorUtils.isDigit(next);
                boolean isSuffixOperatorBeforeOpenBracket = isSuffixOperator(c) && findOpenBracket(next) != -1;
                boolean isSuffixOperatorBeforeFunction = isSuffixOperator(c) && CalculatorUtils.isLetter(next);
                if (
                        isDigitBeforeOpenBracket ||
                                isDigitAfterCloseBracket ||
                                isSuffixOperatorBeforeDigit ||
                                isSuffixOperatorBeforeOpenBracket ||
                                isSuffixOperatorBeforeFunction
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

}
