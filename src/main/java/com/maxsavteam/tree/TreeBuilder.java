package com.maxsavteam.tree;

import com.maxsavteam.exceptions.TreeBuildingException;
import com.maxsavteam.tree.nodes.BracketsNode;
import com.maxsavteam.tree.nodes.NumberNode;
import com.maxsavteam.tree.nodes.OperatorNode;
import com.maxsavteam.tree.nodes.TreeNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Stack;

public class TreeBuilder {

    public static final ArrayList<BracketsType> defaultBrackets = new ArrayList<BracketsType>() {{
        add(new BracketsType('(', ')', 1));
    }};
    public static final ArrayList<BinaryOperator> defaultBinaryOperators = new ArrayList<BinaryOperator>() {{
        add(new BinaryOperator('+', 0));
        add(new BinaryOperator('-', 0));
        add(new BinaryOperator('*', 1));
        add(new BinaryOperator('/', 1));
        add(new BinaryOperator('^', 2));
    }};
    public static final TreeNode emptyNode = new TreeNode();

    private ArrayList<TreeNode> treeNodes;
    private ArrayList<BracketsType> brackets = defaultBrackets;
    private ArrayList<BinaryOperator> operators = defaultBinaryOperators;
    private ArrayList<OperatorPosition> mOperatorPositions;

    public void setBracketsTypes(ArrayList<BracketsType> brackets) {
        this.brackets = brackets;
    }

    public void setBinaryOperators(ArrayList<BinaryOperator> operators) {
        this.operators = operators;
    }

    public ArrayList<TreeNode> buildTree(String expression) {
        treeNodes = new ArrayList<>();
        mOperatorPositions = new ArrayList<>();

        int bracketsLevel = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (isOpenBracket(c))
                bracketsLevel++;
            else if (isCloseBracket(c))
                bracketsLevel--;
            else if (isBinaryOperator(c)) {
                int priority = getOperatorPriority(c);
                mOperatorPositions.add(new OperatorPosition(bracketsLevel, priority, i));
            }
        }

        build(1, expression, 0, 0);

        return treeNodes;
    }

    private void build(int v, String expression, int rootLevel, int exampleOffset) {
        if(expression.length() == 0)
            return;
        String ex = expression;
        int minLevel = getBracketsMinLevel(ex);
        expandTreeToPosition(v);

        if(minLevel >= 1){
            int startBracketType = getBracketType(ex.charAt(0));
            treeNodes.set(v, new BracketsNode(startBracketType));
            build(2 * v, ex.substring(1, ex.length()-1), rootLevel + 1, exampleOffset + 1);
            return;
        }

        int end = exampleOffset + ex.length();
        OperatorPosition foundPos = null, lastPos = null;
        boolean arePrioritiesEqual = true;
        for(OperatorPosition pos : mOperatorPositions){
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

        if(foundPos != null){
            String firstPart = ex.substring(0, foundPos.position - exampleOffset);
            String secondPart = ex.substring(foundPos.position - exampleOffset + 1);

            OperatorNode node = new OperatorNode(ex.charAt(foundPos.position - exampleOffset));
            treeNodes.set(v, node);

            build(2 * v, firstPart, rootLevel, exampleOffset);
            build(2 * v + 1, secondPart, rootLevel, foundPos.position + 1);
        }else{
            NumberNode node = new NumberNode(new BigDecimal(ex));
            treeNodes.set(v, node);
        }
    }

    private void expandTreeToPosition(int pos){
        while(treeNodes.size() <= pos){
            treeNodes.add(emptyNode);
        }
    }

    private int getBracketsMinLevel(String ex) {
        Stack<Integer> typesStack = new Stack<>();
        int minLevel = Integer.MAX_VALUE;

        for(int i = 0; i < ex.length(); i++){
            char c = ex.charAt(i);
            if(isOpenBracket(c)){
                typesStack.push(getBracketType(c));
            }else if(isCloseBracket(c)){
                if(typesStack.isEmpty() || typesStack.peek() != getBracketType(c))
                    throw new TreeBuildingException("Invalid bracket sequence");
                typesStack.pop();
            }else{
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

    private int getBracketType(char c){
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

    public static boolean isNodeEmpty(ArrayList<TreeNode> nodes, int pos){
        return nodes.size() <= pos || nodes.get(pos) == emptyNode;
    }

}
