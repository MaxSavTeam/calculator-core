package com.maxsavteam.calculator;

import com.maxsavteam.calculator.exceptions.CalculatingException;
import com.maxsavteam.calculator.tree.BracketsType;
import javafx.collections.ArrayChangeListener;

import java.util.ArrayList;
import java.util.Stack;

public class ExpressionBracketsChecker {

    public static String tryToCloseExpressionBrackets(String expression, ArrayList<BracketsType> bracketsTypes){
        StringBuilder sbResult = new StringBuilder(expression);

        Stack<Integer> typesStack = new Stack<>();

        for(int i = 0; i < expression.length(); i++){
            boolean isOpenBracket = false;
            boolean isCloseBracket = false;
            int type = 0;
            char c = expression.charAt(i);
            for(BracketsType bracketsType : bracketsTypes){
                if(c == bracketsType.openBracket){
                    isOpenBracket = true;
                    type = bracketsType.type;
                    break;
                }else if(c == bracketsType.closeBracket){
                    isCloseBracket = true;
                    type = bracketsType.type;
                    break;
                }
            }
            if(isOpenBracket){
                typesStack.push(type);
            }else if(isCloseBracket){
                if(typesStack.isEmpty())
                    throw new CalculatingException(CalculatingException.INVALID_BRACKETS_SEQUENCE);
                if(typesStack.peek() != type)
                    throw new CalculatingException(CalculatingException.INVALID_BRACKETS_SEQUENCE);
                else
                    typesStack.pop();
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

}
