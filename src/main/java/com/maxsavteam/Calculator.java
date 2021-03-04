package com.maxsavteam;

import com.maxsavteam.exceptions.CalculatingException;
import com.maxsavteam.resolvers.BinaryOperatorResolver;
import com.maxsavteam.resolvers.BracketsResolver;
import com.maxsavteam.tree.BinaryOperator;
import com.maxsavteam.tree.BracketsType;
import com.maxsavteam.tree.TreeBuilder;
import com.maxsavteam.tree.nodes.BracketsNode;
import com.maxsavteam.tree.nodes.NumberNode;
import com.maxsavteam.tree.nodes.OperatorNode;
import com.maxsavteam.tree.nodes.TreeNode;
import com.maxsavteam.utils.CalculatorUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Calculator {

    private final TreeBuilder builder;
    public final static int roundScale = 8;
    private BinaryOperatorResolver resolver = defaultResolver;
    private BracketsResolver bracketsResolver = defaultBracketsResolver;

    public static final BinaryOperatorResolver defaultResolver = (operator, a, b) -> {
        if(operator == '+')
            return a.add(b);
        if(operator == '-')
            return a.subtract(b);
        if(operator == '*')
            return a.multiply(b);
        if(operator == '/')
            return CalculatorUtils.deleteZeros(a.divide(b, roundScale, RoundingMode.HALF_EVEN));
        if(operator == '^')
            return CalculatorUtils.deleteZeros(CalculatorUtils.pow(a, b));
        throw new CalculatingException("Unknown binary operator '" + operator + "'");
    };

    public static final BracketsResolver defaultBracketsResolver = (type, a) -> a;

    public Calculator(){
        builder = new TreeBuilder();
    }

    public void setBracketsTypes(ArrayList<BracketsType> brackets){
        builder.setBracketsTypes(brackets);
    }

    public void setBinaryOperators(ArrayList<BinaryOperator> operators){
        builder.setBinaryOperators(operators);
    }

    public void setBinaryOperatorResolver(BinaryOperatorResolver resolver) {
        this.resolver = resolver;
    }

    public void setBracketsResolver(BracketsResolver bracketsResolver) {
        this.bracketsResolver = bracketsResolver;
    }

    public BigDecimal calculate(String expression){
        ArrayList<TreeNode> nodes = builder.buildTree(expression);
        return calc(1, nodes);
    }

    private BigDecimal calc(int v, ArrayList<TreeNode> nodes){
         TreeNode node = nodes.get(v);

         if(node instanceof BracketsNode){
             return bracketsResolver.resolve(((BracketsNode) node).getType(), calc(2 * v, nodes));
         }else if(node instanceof NumberNode){
             return ((NumberNode) node).getNumber();
         }
         char symbol = ((OperatorNode) node).getOperator();
         if(TreeBuilder.isNodeEmpty(nodes, 2 * v))
             throw new CalculatingException("Some binary operator does not have left operand");
        if(TreeBuilder.isNodeEmpty(nodes, 2 * v + 1))
            throw new CalculatingException("Some binary operator does not have right operand");
        BigDecimal a = calc(2 * v, nodes);
        BigDecimal b = calc(2 * v + 1, nodes);

        return resolver.calculate(symbol, a, b);
    }

}
