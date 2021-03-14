package com.maxsavteam.calculator.tree;

import com.maxsavteam.calculator.exceptions.TreeBuildingException;
import com.maxsavteam.calculator.tree.nodes.*;
import com.maxsavteam.calculator.utils.CalculatorUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Stack;

public class TreeBuilder {

    public static final ArrayList<BracketsType> defaultBrackets = new ArrayList<BracketsType>() {{
        add(new BracketsType('(', ')', 1));
        add(new BracketsType('[', ']', 2)); // round
        add(new BracketsType('\u23A3', '\u23A6', 3)); // round floor
        add(new BracketsType('\u23A1', '\u23A4', 4)); // round ceil
    }};
    public static final ArrayList<BinaryOperator> defaultBinaryOperators = new ArrayList<BinaryOperator>() {{
        add(new BinaryOperator('+', 0));
        add(new BinaryOperator('-', 0));
        add(new BinaryOperator('*', 1));
        add(new BinaryOperator('/', 1));
        add(new BinaryOperator('^', 2));
    }};
    public static final ArrayList<SuffixOperator> defaultSuffixOperators = new ArrayList<SuffixOperator>(){{
        add(new SuffixOperator('!'));
        add(new SuffixOperator('%'));
    }};
    public static final TreeNode emptyNode = new TreeNode();

    private ArrayList<TreeNode> treeNodes;
    private ArrayList<BracketsType> brackets = defaultBrackets;
    private ArrayList<BinaryOperator> operators = defaultBinaryOperators;
    private ArrayList<SuffixOperator> suffixOperators = defaultSuffixOperators;
    private ArrayList<OperatorPosition> mOperatorPositions;

    public void setBracketsTypes(ArrayList<BracketsType> brackets) {
        this.brackets = brackets;
    }

    public void setBinaryOperators(ArrayList<BinaryOperator> operators) {
        this.operators = operators;
    }

    public void setSuffixOperators(ArrayList<SuffixOperator> suffixOperators) {
        this.suffixOperators = suffixOperators;
    }

    public ArrayList<BracketsType> getBrackets() {
        return brackets;
    }

    public ArrayList<BinaryOperator> getOperators() {
        return operators;
    }

    public ArrayList<SuffixOperator> getSuffixOperators() {
        return suffixOperators;
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

    protected void build(int v, String expression, int rootLevel, int exampleOffset) {
        if(expression.length() == 0)
            return;
        int minLevel = getBracketsMinLevel(expression);
        expandTreeToPosition(v);

        if(minLevel >= 1){
            int startBracketType = getBracketType(expression.charAt(0));
            treeNodes.set(v, new BracketsNode(startBracketType));
            build(2 * v, expression.substring(1, expression.length()-1), rootLevel + 1, exampleOffset + 1);
            return;
        }

        OperatorPosition foundPos = findPosition(expression, exampleOffset, rootLevel);
        if(foundPos != null){
            String firstPart = expression.substring(0, foundPos.position - exampleOffset);
            String secondPart = expression.substring(foundPos.position - exampleOffset + 1);

            OperatorNode node = new OperatorNode(expression.charAt(foundPos.position - exampleOffset));
            treeNodes.set(v, node);

            if(firstPart.isEmpty() && !secondPart.isEmpty()){
                if(node.getOperator() == '+')
                    build(v, secondPart, rootLevel, foundPos.position + 1);
                else if(node.getOperator() == '-'){
                    treeNodes.set(v, new NegativeNumberNode());
                    build(2 * v, secondPart, rootLevel, foundPos.position + 1);
                }
            }else {
                build(2 * v, firstPart, rootLevel, exampleOffset);
                build(2 * v + 1, secondPart, rootLevel, foundPos.position + 1);
            }
        }else{
            if(CalculatorUtils.isLetter(expression.charAt(0))) {
                parseFunc(v, expression, exampleOffset, rootLevel);
            }else if(isSuffixOperator(expression.charAt(expression.length() - 1))){
                parseSuffixOperator(v, expression, exampleOffset, rootLevel);
            }else {
                NumberNode node = new NumberNode(new BigDecimal(expression));
                treeNodes.set(v, node);
            }
        }
    }

    protected void parseSuffixOperator(int v, String ex, int offset, int rootLevel){
        char operator = ex.charAt(ex.length() - 1);
        int i = ex.length() - 1;
        int count = 1;
        while(i >= 1 && ex.charAt(i-1) == operator) {
            count++;
            i--;
        }
        SuffixOperatorNode node = new SuffixOperatorNode(operator, count);
        treeNodes.set(v, node);
        build(2 * v, ex.substring(0, i), offset, rootLevel);
    }

    protected void parseFunc(int v, String ex, int offset, int rootLevel){
        StringBuilder funcName = new StringBuilder();
        int i = 0;
        while(i < ex.length() && CalculatorUtils.isLetter(ex.charAt(i))){
            funcName.append(ex.charAt(i));
            i++;
        }
        if(i == ex.length() || !CalculatorUtils.isDigit(ex.charAt(i))){
            FunctionNode node = new FunctionNode(funcName.toString(), null);
            treeNodes.set(v, node);
            if(!CalculatorUtils.isDigit(ex.charAt(i)))
                build(2 * v, ex.substring(i), rootLevel, offset + i);
        }else{
            StringBuilder suffix = new StringBuilder();
            while(i < ex.length() && CalculatorUtils.isDigit(ex.charAt(i))){
                suffix.append(ex.charAt(i));
                i++;
            }
            BigDecimal a = new BigDecimal(suffix.toString());
            FunctionNode node = new FunctionNode(funcName.toString(), a);
            treeNodes.set(v, node);
            if(i < ex.length()){
                build(2 * v, ex.substring(i), rootLevel, offset + i);
            }
        }
    }

    protected OperatorPosition findPosition(String ex, int offset, int rootLevel){
        int end = offset + ex.length();
        OperatorPosition foundPos = null;
        OperatorPosition lastPos = null;
        boolean arePrioritiesEqual = true;
        for(OperatorPosition pos : mOperatorPositions){
            if(offset > pos.position)
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
        return foundPos;
    }

    protected void expandTreeToPosition(int pos){
        while(treeNodes.size() <= pos){
            treeNodes.add(emptyNode);
        }
    }

    protected int getBracketsMinLevel(String ex) {
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

    private boolean isSuffixOperator(char c){
        for(SuffixOperator operator : suffixOperators){
            if(operator.symbol == c)
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

    public static boolean isNodeEmpty(int v, ArrayList<TreeNode> nodes){
        return nodes.size() <= v || nodes.get(v) == emptyNode;
    }

    public static boolean isNegativeNumberNode(int v, ArrayList<TreeNode> nodes){
        TreeNode node = nodes.get(v);
        if(!(node instanceof OperatorNode))
            return false;
        return isNodeEmpty(2 * v, nodes) && !isNodeEmpty(2 * v + 1, nodes) && ((OperatorNode) node).getOperator() == '-';
    }

}
