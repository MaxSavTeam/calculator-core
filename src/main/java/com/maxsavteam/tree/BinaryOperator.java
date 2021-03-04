package com.maxsavteam.tree;

public class BinaryOperator {

    public final char symbol;
    public final int priority;

    public BinaryOperator(char symbol, int priority) {
        this.symbol = symbol;
        this.priority = priority;
    }
}
