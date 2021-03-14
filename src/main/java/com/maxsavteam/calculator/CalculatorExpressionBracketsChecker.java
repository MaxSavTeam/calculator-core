package com.maxsavteam.calculator;

import com.maxsavteam.calculator.exceptions.CalculatingException;
import com.maxsavteam.calculator.tree.BracketsType;

import java.util.ArrayList;
import java.util.Stack;

public class CalculatorExpressionBracketsChecker {

    private ArrayList<BracketsType> bracketsTypes = new ArrayList<>();

    public void setBracketsTypes(ArrayList<BracketsType> bracketsTypes) {
        this.bracketsTypes = bracketsTypes;
    }

    public String tryToCloseExpressionBrackets(String expression){
        StringBuilder sbResult = new StringBuilder(expression);

        Stack<Integer> typesStack = new Stack<>();

        for(int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);
            int type = findOpenBracket(c);
            boolean isOpenBracket = type != -1;
            if(isOpenBracket){
                typesStack.push(type);
            }else{
                type = findCloseBracket(c);
                if(type != -1) {
                    if (typesStack.isEmpty())
                        throw new CalculatingException(CalculatingException.INVALID_BRACKETS_SEQUENCE);
                    if (typesStack.peek() != type)
                        throw new CalculatingException(CalculatingException.INVALID_BRACKETS_SEQUENCE);
                    else
                        typesStack.pop();
                }
            }
        }
        while(!typesStack.isEmpty()){
            int type = typesStack.pop();
            for(BracketsType bracketsType : bracketsTypes){
                if(bracketsType.type == type)
                    sbResult.append(bracketsType.closeBracket);
            }
        }
        return sbResult.toString();
    }

    private int findOpenBracket(char c){
        for(BracketsType type : bracketsTypes){
            if(c == type.openBracket)
                return type.type;
        }
        return -1;
    }

    private int findCloseBracket(char c){
        for(BracketsType type : bracketsTypes){
            if(c == type.closeBracket)
                return type.type;
        }
        return -1;
    }

}
