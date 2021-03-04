package com.maxsavteam.tree.nodes;

import java.math.BigDecimal;

public class NumberNode extends TreeNode {

    private final BigDecimal number;

    public NumberNode(BigDecimal number) {
        this.number = number;
    }

    public BigDecimal getNumber() {
        return number;
    }
}