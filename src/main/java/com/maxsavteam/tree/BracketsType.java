package com.maxsavteam.tree;

import java.util.Random;

public class BracketsType {

    public final char openBracket;
    public final char closeBracket;
    public final int type;

    public BracketsType(char openBracket, char closeBracket, int type) {
        this.openBracket = openBracket;
        this.closeBracket = closeBracket;

        if(openBracket == closeBracket)
            throw new IllegalArgumentException("Open and close brackets should not be equal");

        this.type = type;
    }
}
