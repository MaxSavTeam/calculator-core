package com.maxsavteam;

import com.maxsavteam.exceptions.CalculatingException;
import com.maxsavteam.resolvers.BinaryOperatorResolver;
import com.maxsavteam.resolvers.BracketsResolver;
import com.maxsavteam.resolvers.FunctionsResolver;
import com.maxsavteam.tree.BinaryOperator;
import com.maxsavteam.tree.BracketsType;
import com.maxsavteam.tree.TreeBuilder;
import com.maxsavteam.tree.nodes.*;
import com.maxsavteam.utils.CalculatorUtils;
import com.maxsavteam.utils.MathUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Calculator {

    private final TreeBuilder builder;
    public final static int roundScale = 8;
    private BinaryOperatorResolver resolver = defaultResolver;
    private BracketsResolver bracketsResolver = defaultBracketsResolver;
    private FunctionsResolver functionsResolver = defaultFunctionsResolver;

    public static final BinaryOperatorResolver defaultResolver = (operator, a, b) -> {
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

    public static final BracketsResolver defaultBracketsResolver = (type, a) -> a;

    public Calculator() {
        builder = new TreeBuilder();
    }

    public void setBracketsTypes(ArrayList<BracketsType> brackets) {
        builder.setBracketsTypes(brackets);
    }

    public void setBinaryOperators(ArrayList<BinaryOperator> operators) {
        builder.setBinaryOperators(operators);
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

    public BigDecimal calculate(String expression) {
        ArrayList<TreeNode> nodes = builder.buildTree(expression);
        return calc(1, nodes);
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
        }
        char symbol = ((OperatorNode) node).getOperator();
        if (TreeBuilder.isNodeEmpty(2 * v, nodes))
            throw new CalculatingException("Some binary operator does not have left operand");
        if (TreeBuilder.isNodeEmpty(2 * v + 1, nodes))
            throw new CalculatingException("Some binary operator does not have right operand");
        BigDecimal a = calc(2 * v, nodes);
        BigDecimal b = calc(2 * v + 1, nodes);

        return resolver.calculate(symbol, a, b);
    }

}
