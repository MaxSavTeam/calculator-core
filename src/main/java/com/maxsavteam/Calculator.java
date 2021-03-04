package com.maxsavteam;

import com.maxsavteam.exceptions.CalculatingException;
import com.maxsavteam.resolvers.BinaryOperatorResolver;
import com.maxsavteam.resolvers.BracketsResolver;
import com.maxsavteam.tree.*;
import com.maxsavteam.tree.nodes.BracketsNode;
import com.maxsavteam.tree.nodes.NumberNode;
import com.maxsavteam.tree.nodes.OperatorNode;
import com.maxsavteam.tree.nodes.TreeNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Calculator {

    private final TreeBuilder builder;
    public final static int roundScale = 8;
    private final BinaryOperatorResolver resolver;
    private final BracketsResolver bracketsResolver;

    public static final BinaryOperatorResolver defaultResolver = (operator, a, b) -> {
        if(operator == '+')
            return a.add(b);
        if(operator == '-')
            return a.subtract(b);
        if(operator == '*')
            return a.multiply(b);
        if(operator == '/')
            return deleteZeros(a.divide(b, roundScale, RoundingMode.HALF_EVEN));
        throw new CalculatingException("Unknown binary operator '" + operator + "'");
    };

    public static final BracketsResolver defaultBracketsResolver = (type, a) -> a;

    public Calculator(){
        this(TreeBuilder.defaultBrackets, TreeBuilder.defaultBinaryOperators, defaultResolver, defaultBracketsResolver);
    }

    public Calculator(ArrayList<BracketsType> brackets,
                      ArrayList<BinaryOperator> binaryOperators,
                      BinaryOperatorResolver resolver,
                      BracketsResolver bracketsResolver){
        builder = new TreeBuilder(brackets, binaryOperators);
        this.resolver = resolver;
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

    private static BigDecimal deleteZeros(BigDecimal b){
        String res = b.toPlainString();
        int len = res.length();
        if ( res.contains( "." ) && res.charAt( len - 1 ) == '0' ) {
            while ( res.charAt( len - 1 ) == '0' ) {
                len--;
                res = res.substring( 0, len );
            }
            if ( res.charAt( len - 1 ) == '.' ) {
                res = res.substring( 0, len - 1 );
            }
        }
        while ( res.charAt( 0 ) == '0' && res.length() > 1 && res.charAt( 1 ) != '.' ) {
            res = res.substring( 1 );
        }
        return new BigDecimal(res);
    }

}
