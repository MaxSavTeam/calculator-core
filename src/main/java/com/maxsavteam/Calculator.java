package com.maxsavteam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Calculator {

    private ArrayList<BasicPosition> mBasicPositions;
    private final Map<Character, Integer> mBasicOperatorsPrioritiesMap = new HashMap<Character, Integer>(){{
        put('+', 0);
        put('-', 0);
        put('*', 1);
        put('/', 1);
    }};
    public final static int roundScale = 8;

    private static class BasicPosition implements Comparable<BasicPosition>{
        private final int level;
        private final int priority;
        private final int position;

        public BasicPosition(int level, int priority, int position) {
            this.level = level;
            this.priority = priority;
            this.position = position;
        }

        @Override
        public int compareTo(BasicPosition o) {
            if (level != o.level)
                return Integer.compare(level, o.level);
            if (priority != o.priority)
                return Integer.compare(priority, o.priority);
            return Integer.compare(position, o.position);
        }
    }

    private String deleteSimpleBrackets(String ex, ObjectHolder<Integer> deletedCount){
        String s = ex;
        int minLevel = Integer.MAX_VALUE;
        int level = 0;
        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if(c == '(')
                level++;
            else if(c == ')')
                level--;
            else
                minLevel = Math.min(minLevel, level);
        }
        int i = 0;
        deletedCount.value = minLevel;
        while(i++ < minLevel)
            s = s.substring(1, s.length()-1);
        return s;
    }

    public BigDecimal calculate(String expression){
        mBasicPositions = new ArrayList<>();

        int bracketsLevel = 0;
        for(int i = 0; i < expression.length(); i++){
            char c = expression.charAt(i);
            if(isOpenBracket(c))
                bracketsLevel++;
            else if(isCloseBracket(c))
                bracketsLevel--;
            else if(isBasicOperator(c)){
                int priority = mBasicOperatorsPrioritiesMap.get(c);
                mBasicPositions.add(new BasicPosition(bracketsLevel, priority, i));
            }
        }
        //Collections.sort(mBasicPositions);

        return calc(expression, 0, 0, mBasicPositions.isEmpty() ? -1 : 0);
    }

    private BigDecimal calc(String ex, int rootLevel, int exampleOffset, int listItemPosition){
        ObjectHolder<Integer> deletedBracketsCount = new ObjectHolder<>(0);
        String e = deleteSimpleBrackets(ex, deletedBracketsCount);
        rootLevel += deletedBracketsCount.value;
        exampleOffset += deletedBracketsCount.value;

        int end = exampleOffset + e.length();
        BasicPosition foundPos = null, lastPos = null;
        boolean arePrioritiesEqual = true;
        for(BasicPosition pos : mBasicPositions){
            if(exampleOffset > pos.position)
                continue;
            if (pos.position >= end) {
                break;
            }
            if(pos.level != rootLevel)
                continue;

            if (foundPos == null)
                foundPos = pos;
            else {
                if(pos.priority != foundPos.priority)
                    arePrioritiesEqual = false;
                if(pos.level < foundPos.level || pos.priority < foundPos.priority){
                    foundPos = pos;
                }
            }
            lastPos = pos;
        }
        if(arePrioritiesEqual)
            foundPos = lastPos;

        if (foundPos != null) {
            String firstPart = e.substring(0, foundPos.position - exampleOffset);
            String secondPart = e.substring(foundPos.position - exampleOffset + 1);

            BigDecimal firstResult = calc(firstPart, rootLevel, exampleOffset, -1);
            int nextItemPosition;
            if(mBasicPositions.size() - 1 == listItemPosition){
                nextItemPosition = -1;
            }else{
                nextItemPosition = listItemPosition + 1;
            }
            BigDecimal secondResult = calc(secondPart, rootLevel, foundPos.position + 1, nextItemPosition);

            return resolveBasicOperator(
                    e.charAt(foundPos.position - exampleOffset),
                    firstResult,
                    secondResult
            );
        }

        return new BigDecimal(e);
    }

    private BigDecimal resolveBasicOperator(char c, BigDecimal a, BigDecimal b){
        if(c == '+')
            return a.add(b);
        if(c == '-')
            return a.subtract(b);
        if(c == '*')
            return a.multiply(b);
        if(c == '/')
            return a.divide(b, roundScale, RoundingMode.HALF_EVEN);
        throw new IllegalArgumentException("Operator '" + c + "' not a basic operator");
    }

    private boolean isOpenBracket(char c){
        return c == '(';
    }

    private boolean isCloseBracket(char c){
        return c == ')';
    }

    private boolean isBasicOperator(char c){
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

}
