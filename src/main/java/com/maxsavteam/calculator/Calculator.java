package com.maxsavteam.calculator;

import com.maxsavteam.calculator.exceptions.CalculatingException;
import com.maxsavteam.calculator.resolvers.BinaryOperatorResolver;
import com.maxsavteam.calculator.resolvers.BracketsResolver;
import com.maxsavteam.calculator.resolvers.FunctionsResolver;
import com.maxsavteam.calculator.resolvers.SuffixOperatorResolver;
import com.maxsavteam.calculator.tree.BinaryOperator;
import com.maxsavteam.calculator.tree.BracketsType;
import com.maxsavteam.calculator.tree.SuffixOperator;
import com.maxsavteam.calculator.tree.TreeBuilder;
import com.maxsavteam.calculator.tree.nodes.*;
import com.maxsavteam.calculator.utils.CalculatorUtils;
import com.maxsavteam.calculator.utils.MathUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Calculator {

    public static final String PI_SIGN = "\u03C0";
    public static final String FI_SIGN = "\u03C6";
    public static final String E_SIGN = "\u0190";

    public static final String VERSION = "1.0-rc9";

    private final TreeBuilder builder;
    private final CalculatorExpressionTokenizer mExpressionTokenizer;
    private final CalculatorExpressionFormatter mBracketsChecker;
    public final static int roundScale = 8;
    private BinaryOperatorResolver resolver = defaultResolver;
    private BracketsResolver bracketsResolver = defaultBracketsResolver;
    private FunctionsResolver functionsResolver = defaultFunctionsResolver;
    private SuffixOperatorResolver suffixResolver = defaultSuffixResolver;

    public static final Map<String, String> defaultReplacementMap = new HashMap<String, String>() {{
        put(PI_SIGN, "(" + MathUtils.PI.toPlainString() + ")");
        put(FI_SIGN, "(" + MathUtils.FI.toPlainString() + ")");
        put(E_SIGN, "(" + MathUtils.E.toPlainString() + ")");
        put("e", "(" + MathUtils.E.toPlainString() + ")");
    }};

    public static final BinaryOperatorResolver defaultResolver = new BinaryOperatorResolver() {
        @Override
        public @NotNull BigDecimal calculate(char operator, BigDecimal a, BigDecimal b) {
            if (operator == '+')
                return a.add(b);
            if (operator == '-')
                return a.subtract(b);
            if (operator == '*')
                return a.multiply(b);
            if (operator == '/')
                if (b.signum() == 0)
                    throw new CalculatingException(CalculatingException.DIVISION_BY_ZERO);
                else
                    return CalculatorUtils.removeZeros(a.divide(b, roundScale, RoundingMode.HALF_EVEN));
            if (operator == '^')
                return CalculatorUtils.removeZeros(MathUtils.pow(a, b));
            throw new CalculatingException(CalculatingException.INVALID_BINARY_OPERATOR);
        }

        @Override
        public @NotNull BigDecimal calculatePercent(char binaryOperator, BigDecimal a, BigDecimal percent) {
            BigDecimal percentOfNum = a.multiply(percent); // percent already divided by zero
            if (binaryOperator == '+')
                return a.add(percentOfNum);
            else if (binaryOperator == '-')
                return a.subtract(percentOfNum);
            else if (binaryOperator == '*')
                return percentOfNum;
            else if (binaryOperator == '/')
                if (percent.signum() == 0)
                    throw new CalculatingException(CalculatingException.DIVISION_BY_ZERO);
                else
                    return a.divide(percent, roundScale, RoundingMode.HALF_EVEN);
            throw new CalculatingException(CalculatingException.INVALID_OPERATOR_FOR_PERCENT);
        }
    };

    public static final FunctionsResolver defaultFunctionsResolver = (funcName, suffix, operand) -> {
        if (suffix == null && operand == null) {
            throw new CalculatingException(CalculatingException.FUNCTION_SUFFIX_AND_OPERAND_NULL);
        }
        BigDecimal notNullNum = suffix == null ? operand : suffix;
        switch (funcName) {
            case "log":
                if (suffix != null) {
                    if (operand != null)
                        return MathUtils.logWithBase(operand, suffix);
                    else
                        return MathUtils.log(suffix);
                } else {
                    return MathUtils.log(operand);
                }
            case "cos":
                return MathUtils.cos(notNullNum);
            case "sin":
                return MathUtils.sin(notNullNum);
            case "tan":
                return MathUtils.tan(notNullNum);
            case "ln":
                return MathUtils.ln(notNullNum);
            case "R":
            case "sqrt":
                return MathUtils.rootWithBase(notNullNum, BigDecimal.valueOf(2));
            case "abs":
                return MathUtils.abs(notNullNum);
            default:
                throw new CalculatingException(CalculatingException.UNKNOWN_FUNCTION);
        }
    };

    public static final BracketsResolver defaultBracketsResolver = (type, a) -> {
        if (type == 1)
            return a;
        else if (type == 2)
            return MathUtils.round(a);
        else if (type == 3)
            return MathUtils.floor(a);
        else if (type == 4)
            return MathUtils.ceil(a);
        throw new CalculatingException(CalculatingException.UNKNOWN_BRACKET_TYPE);
    };

    public static final SuffixOperatorResolver defaultSuffixResolver = (operator, count, operand) -> {
        if (operator == '!')
            return MathUtils.fact(operand, count);
        else if (operator == '%')
            return operand.divide(BigDecimal.valueOf(100), roundScale, RoundingMode.HALF_EVEN);
        throw new CalculatingException(CalculatingException.UNKNOWN_SUFFIX_OPERATOR);
    };

    public Calculator() {
        builder = new TreeBuilder();

        mBracketsChecker = new CalculatorExpressionFormatter();
        mBracketsChecker.setBracketsTypes(TreeBuilder.defaultBrackets);
        mBracketsChecker.setSuffixOperators(TreeBuilder.defaultSuffixOperators);

        mExpressionTokenizer = new CalculatorExpressionTokenizer();
        mExpressionTokenizer.setReplacementMap(defaultReplacementMap);
    }

    /**
     * Sets brackets for TreeBuilder
     **/
    public void setBracketsTypes(ArrayList<BracketsType> brackets) {
        builder.setBracketsTypes(brackets);
        mBracketsChecker.setBracketsTypes(brackets);
    }

    /**
     * Sets binary operators for TreeBuilder
     **/
    public void setBinaryOperators(ArrayList<BinaryOperator> operators) {
        builder.setBinaryOperators(operators);
    }

    /**
     * Sets suffix operators for TreeBuilder
     **/
    public void setSuffixOperators(ArrayList<SuffixOperator> operators) {
        builder.setSuffixOperators(operators);
        mBracketsChecker.setSuffixOperators(operators);
    }

    /**
     * Sets custom binary operators resolver
     */
    public void setBinaryOperatorResolver(BinaryOperatorResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Sets custom brackets resolver
     */
    public void setBracketsResolver(BracketsResolver bracketsResolver) {
        this.bracketsResolver = bracketsResolver;
    }

    /**
     * Sets custom functions resolver
     */
    public void setFunctionsResolver(FunctionsResolver functionsResolver) {
        this.functionsResolver = functionsResolver;
    }

    /**
     * Sets custom suffix operators resolver
     */
    public void setSuffixResolver(SuffixOperatorResolver suffixResolver) {
        this.suffixResolver = suffixResolver;
    }

    /**
     * Sets custom aliases for tokenizer
     */
    public void setAliases(Map<String, String> map) {
        mExpressionTokenizer.setReplacementMap(map);
    }

    /**
     * Calculates answer of expression
     */
    public BigDecimal calculate(String expression) {
        String expr = CalculatorUtils.removeSpaces(expression);
        expr = mExpressionTokenizer.tokenizeExpression(expr);
        expr = mBracketsChecker.tryToCloseExpressionBrackets(expr);
        expr = mBracketsChecker.formatNearBrackets(expr);
        ArrayList<TreeNode> nodes = builder.buildTree(expr);
        return CalculatorUtils.removeZeros(calc(0, nodes));
    }

    private BigDecimal calc(int v, ArrayList<TreeNode> nodes) {
        TreeNode node = nodes.get(v);

        if (node instanceof BracketsNode) {
            return bracketsResolver.resolve(((BracketsNode) node).getType(), calc(node.getLeftSonIndex(), nodes));
        } else if (node instanceof NumberNode) {
            return ((NumberNode) node).getNumber();
        } else if (node instanceof NegativeNumberNode) {
            return calc(node.getLeftSonIndex(), nodes).multiply(BigDecimal.valueOf(-1));
        } else if (node instanceof FunctionNode) {
            return processFunction(v, nodes);
        } else if (node instanceof SuffixOperatorNode) {
            SuffixOperatorNode suffixNode = (SuffixOperatorNode) node;
            if (TreeBuilder.isNodeEmpty(node.getLeftSonIndex(), nodes))
                throw new CalculatingException(CalculatingException.NO_OPERAND_FOR_SUFFIX_OPERATOR);
            return suffixResolver.resolve(suffixNode.operator, suffixNode.count, calc(node.getLeftSonIndex(), nodes));
        } else if (node instanceof OperatorNode) {
            return processOperatorNode(v, nodes);
        } else {
            throw new CalculatingException(CalculatingException.REQUESTED_EMPTY_NODE);
        }
    }

    protected BigDecimal processOperatorNode(int v, ArrayList<TreeNode> nodes) {
        TreeNode node = nodes.get(v);
        char symbol = ((OperatorNode) node).getOperator();
        if (TreeBuilder.isNodeEmpty(node.getLeftSonIndex(), nodes) || TreeBuilder.isNodeEmpty(node.getRightSonIndex(), nodes))
            throw new CalculatingException(CalculatingException.INVALID_BINARY_OPERATOR);

        BigDecimal a = calc(node.getLeftSonIndex(), nodes);
        BigDecimal b = calc(node.getRightSonIndex(), nodes);
        TreeNode rightNode = nodes.get(node.getRightSonIndex());
        if (rightNode instanceof SuffixOperatorNode) {
            SuffixOperatorNode suffix = (SuffixOperatorNode) rightNode;
            if (suffix.operator == '%')
                return resolver.calculatePercent(symbol, a, b);
        }

        return resolver.calculate(symbol, a, b);
    }

    protected BigDecimal processFunction(int v, ArrayList<TreeNode> nodes) {
        FunctionNode functionNode = (FunctionNode) nodes.get(v);
        if (functionNode.funcName.equals("A"))
            return processAverage(v, nodes);
        BigDecimal operand = null;
        if (!TreeBuilder.isNodeEmpty(functionNode.getLeftSonIndex(), nodes))
            operand = calc(functionNode.getLeftSonIndex(), nodes);
        return functionsResolver.resolve(functionNode.funcName, functionNode.suffix, operand);
    }

    protected BigDecimal processAverage(int v, ArrayList<TreeNode> nodes) {
        BigDecimal sum = calc(nodes.get(v).getLeftSonIndex(), nodes);
        int count = 1;
        v = nodes.get(nodes.get(v).getLeftSonIndex()).getLeftSonIndex();
        while (true) {
            count++;
            int leftSon = nodes.get(v).getLeftSonIndex();
            int rightSon = nodes.get(v).getRightSonIndex();
            if (TreeBuilder.isNodeEmpty(leftSon, nodes) || TreeBuilder.isNodeEmpty(rightSon, nodes))
                break;
            TreeNode left = nodes.get(leftSon);
            TreeNode right = nodes.get(rightSon);
            if (!(left instanceof OperatorNode) && !(right instanceof OperatorNode))
                break;
            int next = -1;
            if (left instanceof OperatorNode) {
                OperatorNode node = (OperatorNode) left;
                if (node.getOperator() == '-' || node.getOperator() == '+')
                    next = leftSon;
            }
            if (right instanceof OperatorNode) {
                OperatorNode node = (OperatorNode) right;
                if (node.getOperator() == '-' || node.getOperator() == '+')
                    next = rightSon;
            }
            if (next == -1)
                break;
            else
                v = next;
        }
        return sum.divide(BigDecimal.valueOf(count), roundScale, RoundingMode.HALF_EVEN);
    }

}
