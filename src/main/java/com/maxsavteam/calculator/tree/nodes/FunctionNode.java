package com.maxsavteam.calculator.tree.nodes;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class FunctionNode extends TreeNode {

    public final String funcName;
    @Nullable
    public final BigDecimal suffix;

    public FunctionNode(String funcName, @Nullable BigDecimal suffix) {
        this.funcName = funcName;
        this.suffix = suffix;
    }
}
