package com.maxsavteam.calculator.tree;

import com.maxsavteam.calculator.exceptions.CalculatingException;
import com.maxsavteam.calculator.exceptions.TreeBuildingException;
import com.maxsavteam.calculator.tree.nodes.BracketsNode;
import com.maxsavteam.calculator.tree.nodes.FunctionNode;
import com.maxsavteam.calculator.tree.nodes.NegativeNumberNode;
import com.maxsavteam.calculator.tree.nodes.NumberNode;
import com.maxsavteam.calculator.tree.nodes.OperatorNode;
import com.maxsavteam.calculator.tree.nodes.SuffixOperatorNode;
import com.maxsavteam.calculator.tree.nodes.TreeNode;
import com.maxsavteam.calculator.utils.CalculatorUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Builds tree from expression.<br>
 *
 * A tree is an array, where each node has its own index in this array<br>
 * First node has index 0.<br>
 * If node is not a {@link NumberNode}, it will have at least one son.<br>
 * You can get left and right son index in tree array of node with {@link TreeNode#getLeftSonIndex()} and {@link TreeNode#getRightSonIndex()} respectively.<br>
 *
 * Parser based on numbers, operators and functions. Operator can be binary (+, -, *, /, ^ (power)),<br>
 * bracket or suffix (this operators follow after number (e.g. % (percent), ! (factorial)).<br>
 * All of this operators you can specify.
 *
 * <h3>Binary operators</h3>
 * All binary operators have two sons - left operand and right operand.<br>
 * But if - operator has only left son, then {@link OperatorNode} will be replaced with {@link NegativeNumberNode}.
 * Also if + operator has only right son, then this node will be skipped.
 *
 * <h3>Brackets operators</h3>
 * {@link BracketsNode} contain type of bracket and has only one son with index 2 * v<br>
 *
 * <h3>Suffix operators</h3>
 * {@link SuffixOperatorNode} contain symbol of operator and count and will be applied to result of 2 * v node<br>
 *
 * <h3>Numbers</h3>
 * {@link NumberNode} is a leaf of tree and does not have any son.<br>
 *
 * <h3>Functions</h3>
 * {@link FunctionNode} contain name of function. All letters will be parsed as functions.
 * Function also has suffix. It is number straight after name of function
 * (e.g. "sin45", sin will be there as function name and 45 as suffix)<br>
 * If function name of suffix followed by some type of bracket, then {@link FunctionNode} will have
 * one brackets son with all rules of {@link BracketsNode} described above.
 * */
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

    private int currentIndex = 0;

    private int nextIndex(){
        treeNodes.add(emptyNode);
        return currentIndex++;
    }

    /**
     * Sets custom brackets
     * */
    public void setBracketsTypes(ArrayList<BracketsType> brackets) {
        this.brackets = brackets;
    }

    /**
     * Sets custom binary operators
     * */
    public void setBinaryOperators(ArrayList<BinaryOperator> operators) {
        this.operators = operators;
    }

    /**
     * Sets custom suffix operators
     * */
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

    /**
     * Builds tree from expression.<br>
     *
     * Parses this expression and resolves binary operators
     * */
    public ArrayList<TreeNode> buildTree(String expression) {
        treeNodes = new ArrayList<>();
        currentIndex = 0;
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

        build(nextIndex(), expression, 0, 0);

        return treeNodes;
    }

    /**
     * @param v Current node index
     * @param expression Current part of original expression
     * @param rootLevel Current level of brackets.
     *                  Necessary, because expression is cut, but in operatorPositions
     *                  old positions of this operators
     * @param exampleOffset Index of this expression in original expression.
     *                      Necessary, because expression is cut, but in operatorPositions
     *                      old positions of this operators
     * */
    protected void build(int v, String expression, int rootLevel, int exampleOffset) {
        if(expression.length() == 0)
            return;
        int minLevel = getBracketsMinLevel(expression);
        expandTreeToPosition(v);

        if(minLevel >= 1){
            int startBracketType = getBracketType(expression.charAt(0));
            BracketsNode node = new BracketsNode(startBracketType);
            node.setLeftSonIndex(nextIndex());
            treeNodes.set(v, node);
            build(node.getLeftSonIndex(), expression.substring(1, expression.length()-1), rootLevel + 1, exampleOffset + 1);
            return;
        }

        OperatorPosition foundPos = findPosition(expression, exampleOffset, rootLevel);
        if(foundPos != null){
            String firstPart = expression.substring(0, foundPos.position - exampleOffset);
            String secondPart = expression.substring(foundPos.position - exampleOffset + 1);

            OperatorNode node = new OperatorNode(expression.charAt(foundPos.position - exampleOffset));
            treeNodes.set(v, node);

            if(firstPart.isEmpty() && !secondPart.isEmpty()){
                if(node.getOperator() == '+') {
                    build(v, secondPart, rootLevel, foundPos.position + 1);
                }else if(node.getOperator() == '-'){
                    NegativeNumberNode negativeNumberNode = new NegativeNumberNode();
                    treeNodes.set(v, negativeNumberNode);
                    negativeNumberNode.setLeftSonIndex(nextIndex());
                    build(negativeNumberNode.getLeftSonIndex(), secondPart, rootLevel, foundPos.position + 1);
                }
            }else {
                node.setLeftSonIndex(nextIndex());
                node.setRightSonIndex(nextIndex());
                build(node.getLeftSonIndex(), firstPart, rootLevel, exampleOffset);
                build(node.getRightSonIndex(), secondPart, rootLevel, foundPos.position + 1);
            }
        }else{
            if(CalculatorUtils.isLetter(expression.charAt(0))) {
                parseFunc(v, expression, exampleOffset, rootLevel);
            }else if(isSuffixOperator(expression.charAt(expression.length() - 1))){
                parseSuffixOperator(v, expression, exampleOffset, rootLevel);
            }else {
                try {
                    NumberNode node = new NumberNode(new BigDecimal(expression));
                    treeNodes.set(v, node);
                }catch (NumberFormatException e){
                    throw new CalculatingException(CalculatingException.NUMBER_FORMAT_EXCEPTION, e);
                }
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
        node.setLeftSonIndex(nextIndex());
        treeNodes.set(v, node);
        build(node.getLeftSonIndex(), ex.substring(0, i), offset, rootLevel);
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
            if(i != ex.length() && !CalculatorUtils.isDigit(ex.charAt(i))) {
                node.setLeftSonIndex(nextIndex());
                build(node.getLeftSonIndex(), ex.substring(i), rootLevel, offset + i);
            }
        }else{
            StringBuilder suffix = new StringBuilder();
            while(i < ex.length() && (CalculatorUtils.isDigit(ex.charAt(i)) || ex.charAt(i) == '.')){
                suffix.append(ex.charAt(i));
                i++;
            }
            BigDecimal a = new BigDecimal(suffix.toString());
            FunctionNode node = new FunctionNode(funcName.toString(), a);
            treeNodes.set(v, node);
            if(i < ex.length()){
                node.setLeftSonIndex(nextIndex());
                build(node.getLeftSonIndex(), ex.substring(i), rootLevel, offset + i);
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
                    throw new TreeBuildingException(CalculatingException.INVALID_BRACKETS_SEQUENCE);
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
        return v == -1 || nodes.size() <= v || nodes.get(v) == emptyNode;
    }

}
