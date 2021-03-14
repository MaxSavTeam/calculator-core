package com.maxsavteam.calculator;

import java.util.Map;

public class CalculatorExpressionTokenizer {

    private final Map<String, String> mReplacementMap;

    public CalculatorExpressionTokenizer(Map<String, String> replacementMap) {
        mReplacementMap = replacementMap;
    }

    public String formatExpression(String expression) {
        String expr = expression;
        for(Map.Entry<String, String> entry : mReplacementMap.entrySet()){
            expr = expr.replaceAll(entry.getValue(), entry.getKey());
        }
        return expr;
    }
}
