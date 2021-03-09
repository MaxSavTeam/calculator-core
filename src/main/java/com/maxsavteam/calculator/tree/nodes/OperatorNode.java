package com.maxsavteam.calculator.tree.nodes;

public class OperatorNode extends TreeNode {

    private final char symbol;

    public OperatorNode(char symbol) {
        this.symbol = symbol;
    }

    public char getOperator() {
        return symbol;
    }
}
