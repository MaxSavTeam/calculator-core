package com.maxsavteam;

import com.maxsavteam.exceptions.CalculatingException;
import com.maxsavteam.resolvers.BinaryOperatorResolver;
import com.maxsavteam.resolvers.BracketsResolver;
import com.maxsavteam.resolvers.FunctionsResolver;
import com.maxsavteam.resolvers.SuffixOperatorResolver;
import com.maxsavteam.tree.BinaryOperator;
import com.maxsavteam.tree.BracketsType;
import com.maxsavteam.tree.SuffixOperator;
import com.maxsavteam.tree.TreeBuilder;
import com.maxsavteam.tree.nodes.*;
import com.maxsavteam.utils.CalculatorUtils;
import com.maxsavteam.utils.MathUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Calculator {

    private final TreeBuilder builder;
    public final static int roundScale = 8;
    private BinaryOperatorResolver resolver = defaultResolver;
    private BracketsResolver bracketsResolver = defaultBracketsResolver;
    private FunctionsResolver functionsResolver = defaultFunctionsResolver;
    private SuffixOperatorResolver suffixResolver = defaultSuffixResolver;

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
                return CalculatorUtils.deleteZeros(a.divide(b, roundScale, RoundingMode.HALF_EVEN));
            if (operator == '^')
                return CalculatorUtils.deleteZeros(MathUtils.pow(a, b));
            throw new CalculatingException("Unknown binary operator '" + operator + "'");
        }

        @Override
        public @NotNull BigDecimal processPercent(char binaryOperator, BigDecimal a, BigDecimal percent) {
            BigDecimal percentOfNum = a.multiply(percent); // percent already divided by zero
            if(binaryOperator == '+')
                return a.add(percentOfNum);
            else if(binaryOperator == '-')
                return a.subtract(percentOfNum);
            else if(binaryOperator == '*')
                return percentOfNum;
            else if(binaryOperator == '/')
                return a.divide(percent, roundScale, RoundingMode.HALF_EVEN);
            throw new CalculatingException("Invalid operator for percent");
        }
    };

    public static final FunctionsResolver defaultFunctionsResolver = (funcName, suffix, operand) -> {
        if(suffix == null && operand == null){
            throw new CalculatingException("Functions suffix and operand are both null");
        }
        switch (funcName) {
            case "log":
                if (suffix != null) {
                    if(operand != null)
                        return MathUtils.logWithBase(operand, suffix);
                    else
                        return MathUtils.log(suffix);
                } else {
                    return MathUtils.log(operand);
                }
            case "cos":
                return MathUtils.cos(suffix == null ? operand : suffix);
            case "sin":
                return MathUtils.sin(suffix == null ? operand : suffix);
            case "tan":
                return MathUtils.tan(suffix == null ? operand : suffix);
            case "ln":
                return MathUtils.ln(suffix == null ? operand : suffix);
            case "R":
            case "sqrt":
                return MathUtils.rootWithBase(suffix == null ? operand : suffix, BigDecimal.valueOf(2));
            default:
                throw new CalculatingException("Unknown function " + funcName);
        }
    };

    public static final BracketsResolver defaultBracketsResolver = (type, a) -> {
        if(type == 1)
            return a;
        else if(type == 2)
            return MathUtils.round(a);
        else if(type == 3)
            return MathUtils.floor(a);
        else if(type == 4)
            return MathUtils.ceil(a);
        throw new CalculatingException("Unknown bracket type");
    };

    public static final SuffixOperatorResolver defaultSuffixResolver = (operator, count, operand) -> {
        if(operator == '!')
            return MathUtils.fact(operand, count);
        else if(operator == '%')
            return operand.divide(BigDecimal.valueOf(100), roundScale, RoundingMode.HALF_EVEN);
        throw new CalculatingException("Unknown suffix operator");
    };

    public Calculator() {
        builder = new TreeBuilder();
    }

    public void setBracketsTypes(ArrayList<BracketsType> brackets) {
        builder.setBracketsTypes(brackets);
    }

    public void setBinaryOperators(ArrayList<BinaryOperator> operators) {
        builder.setBinaryOperators(operators);
    }

    public void setSuffixOperators(ArrayList<SuffixOperator> operators){
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

    public BigDecimal calculate(String expression) {
        ArrayList<TreeNode> nodes = builder.buildTree(expression);
        return CalculatorUtils.deleteZeros(calc(1, nodes));
    }

    private BigDecimal calc(int v, ArrayList<TreeNode> nodes) {
        TreeNode node = nodes.get(v);

        if (node instanceof BracketsNode) {
            return bracketsResolver.resolve(((BracketsNode) node).getType(), calc(2 * v, nodes));
        } else if (node instanceof NumberNode) {
            return ((NumberNode) node).getNumber();
        }else if(node instanceof FunctionNode){
            FunctionNode functionNode = (FunctionNode) node;
            BigDecimal operand = null;
            if(!TreeBuilder.isNodeEmpty(2 * v, nodes))
                operand = calc(2 * v, nodes);
            return functionsResolver.resolve(functionNode.funcName, functionNode.suffix, operand);
        }else if(node instanceof SuffixOperatorNode){
            SuffixOperatorNode suffixNode = (SuffixOperatorNode) node;
            if(TreeBuilder.isNodeEmpty(2 * v, nodes))
                throw new CalculatingException("No operand for suffix operator");
            return suffixResolver.resolve(suffixNode.operator, suffixNode.count, calc(2 * v, nodes));
        }else if(node instanceof OperatorNode) {
            return processOperatorNode(v, nodes);
        }else{
            throw new CalculatingException("Requested empty node");
        }
    }

    protected BigDecimal processOperatorNode(int v, ArrayList<TreeNode> nodes){
        char symbol = ((OperatorNode) nodes.get(v)).getOperator();
        if (TreeBuilder.isNodeEmpty(2 * v, nodes))
            throw new CalculatingException("Some binary operator does not have left operand");
        if (TreeBuilder.isNodeEmpty(2 * v + 1, nodes))
            throw new CalculatingException("Some binary operator does not have right operand");

        BigDecimal a = calc(2 * v, nodes);
        BigDecimal b = calc(2 * v + 1, nodes);
        TreeNode rightNode = nodes.get(2 * v + 1);
        if(rightNode instanceof SuffixOperatorNode){
            SuffixOperatorNode suffix = (SuffixOperatorNode) rightNode;
            if(suffix.operator == '%')
                return resolver.processPercent(symbol, a, b);
        }

        return resolver.calculate(symbol, a, b);
    }

}
