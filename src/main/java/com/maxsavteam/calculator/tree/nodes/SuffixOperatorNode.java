package com.maxsavteam.calculator.tree.nodes;

public class SuffixOperatorNode extends TreeNode {

    public final char operator;
    public final int count;

    public SuffixOperatorNode(char operator, int count){
        this.operator = operator;
        this.count = count;
    }

}
