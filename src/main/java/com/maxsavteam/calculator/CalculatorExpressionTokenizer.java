package com.maxsavteam.calculator;

import java.util.Map;

public class CalculatorExpressionTokenizer {

    private Map<String, String> mReplacementMap;

    public void setReplacementMap(Map<String, String> replacementMap) {
        mReplacementMap = replacementMap;
    }

    public String tokenizeExpression(String expression) {
        String expr = expression;
        for(Map.Entry<String, String> entry : mReplacementMap.entrySet()){
            expr = expr.replaceAll(entry.getKey(), entry.getValue());
        }
        return expr;
    }
}
