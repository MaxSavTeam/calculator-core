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

    private final TreeBuilder builder;
    private final CalculatorExpressionTokenizer mExpressionTokenizer;
    private final CalculatorExpressionBracketsChecker mBracketsChecker;
    public final static int roundScale = 8;
    private BinaryOperatorResolver resolver = defaultResolver;
    private BracketsResolver bracketsResolver = defaultBracketsResolver;
    private FunctionsResolver functionsResolver = defaultFunctionsResolver;
    private SuffixOperatorResolver suffixResolver = defaultSuffixResolver;

    public static final Map<String, String> defaultReplacementMap = new HashMap<String, String>(){{
        put("(" + MathUtils.PI.toPlainString() + ")", PI_SIGN);
        put("(" + MathUtils.FI.toPlainString() + ")", FI_SIGN);
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
                    return CalculatorUtils.deleteZeros(a.divide(b, roundScale, RoundingMode.HALF_EVEN));
            if (operator == '^')
                return CalculatorUtils.deleteZeros(MathUtils.pow(a, b));
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

        mBracketsChecker = new CalculatorExpressionBracketsChecker();
        mBracketsChecker.setBracketsTypes(TreeBuilder.defaultBrackets);

        mExpressionTokenizer = new CalculatorExpressionTokenizer();
        mExpressionTokenizer.setReplacementMap(defaultReplacementMap);
    }

    public void setBracketsTypes(ArrayList<BracketsType> brackets) {
        builder.setBracketsTypes(brackets);
        mBracketsChecker.setBracketsTypes(brackets);
    }

    public void setBinaryOperators(ArrayList<BinaryOperator> operators) {
        builder.setBinaryOperators(operators);
    }

    public void setSuffixOperators(ArrayList<SuffixOperator> operators) {
        builder.setSuffixOperators(operators);
    }

    public void setBinaryOperatorResolver(BinaryOperatorResolver resolver) {
        this.resolver = resolver;
    }

    public void setBracketsResolver(BracketsResolver bracketsResolver) {
        this.bracketsResolver = bracketsResolver;
    }

    public void setFunctionsResolver(FunctionsResolver functionsResolver) {
        this.functionsResolver = functionsResolver;
    }

    public void setSuffixResolver(SuffixOperatorResolver suffixResolver) {
        this.suffixResolver = suffixResolver;
    }

    public void setAliases(Map<String, String> map){
        mExpressionTokenizer.setReplacementMap(map);
    }

    public BigDecimal calculate(String expression) {
        String expr = mExpressionTokenizer.tokenizeExpression(expression);
        expr = mBracketsChecker.tryToCloseExpressionBrackets(expr);
        expr = mBracketsChecker.formatNearBrackets(expr);
        ArrayList<TreeNode> nodes = builder.buildTree(expr);
        return CalculatorUtils.deleteZeros(calc(1, nodes));
    }

    private BigDecimal calc(int v, ArrayList<TreeNode> nodes) {
        TreeNode node = nodes.get(v);

        if (node instanceof BracketsNode) {
            return bracketsResolver.resolve(((BracketsNode) node).getType(), calc(2 * v, nodes));
        } else if (node instanceof NumberNode) {
            return ((NumberNode) node).getNumber();
        } else if (node instanceof NegativeNumberNode) {
            return calc(2 * v, nodes).multiply(BigDecimal.valueOf(-1));
        } else if (node instanceof FunctionNode) {
            return processFunction(v, nodes);
        } else if (node instanceof SuffixOperatorNode) {
            SuffixOperatorNode suffixNode = (SuffixOperatorNode) node;
            if (TreeBuilder.isNodeEmpty(2 * v, nodes))
                throw new CalculatingException(CalculatingException.NO_OPERAND_FOR_SUFFIX_OPERATOR);
            return suffixResolver.resolve(suffixNode.operator, suffixNode.count, calc(2 * v, nodes));
        } else if (node instanceof OperatorNode) {
            return processOperatorNode(v, nodes);
        } else {
            throw new CalculatingException(CalculatingException.REQUESTED_EMPTY_NODE);
        }
    }

    protected BigDecimal processOperatorNode(int v, ArrayList<TreeNode> nodes) {
        char symbol = ((OperatorNode) nodes.get(v)).getOperator();
        if (TreeBuilder.isNodeEmpty(2 * v + 1, nodes) || TreeBuilder.isNodeEmpty(2 * v, nodes))
            throw new CalculatingException(CalculatingException.INVALID_BINARY_OPERATOR);

        BigDecimal a = calc(2 * v, nodes);
        BigDecimal b = calc(2 * v + 1, nodes);
        TreeNode rightNode = nodes.get(2 * v + 1);
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
        if (!TreeBuilder.isNodeEmpty(2 * v, nodes))
            operand = calc(2 * v, nodes);
        return functionsResolver.resolve(functionNode.funcName, functionNode.suffix, operand);
    }

    protected BigDecimal processAverage(int v, ArrayList<TreeNode> nodes) {
        BigDecimal sum = calc(2 * v, nodes);
        int count = 1;
        v *= 4;
        while (true) {
            count++;
            if (TreeBuilder.isNodeEmpty(2 * v, nodes) || TreeBuilder.isNodeEmpty(2 * v + 1, nodes))
                break;
            TreeNode left = nodes.get(2 * v);
            TreeNode right = nodes.get(2 * v + 1);
            if (!(left instanceof OperatorNode) && !(right instanceof OperatorNode))
                break;
            int next = -1;
            if (left instanceof OperatorNode) {
                OperatorNode node = (OperatorNode) left;
                if (node.getOperator() == '-' || node.getOperator() == '+')
                    next = 2 * v;
            }
            if (right instanceof OperatorNode) {
                OperatorNode node = (OperatorNode) right;
                if (node.getOperator() == '-' || node.getOperator() == '+')
                    next = 2 * v + 1;
            }
            if (next == -1)
                break;
            else
                v = next;
        }
        return sum.divide(BigDecimal.valueOf(count), roundScale, RoundingMode.HALF_EVEN);
    }

}
