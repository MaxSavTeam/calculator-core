package com.maxsavteam.calculator.tree.nodes;

public class TreeNode {

    private int leftSonIndex = -1;
    private int rightSonIndex = -1;

    public void setLeftSonIndex(int leftSonIndex) {
        if(this.leftSonIndex == -1)
            this.leftSonIndex = leftSonIndex;
    }

    public void setRightSonIndex(int rightSonIndex) {
        if(this.rightSonIndex == -1)
            this.rightSonIndex = rightSonIndex;
    }

    public int getLeftSonIndex() {
        return leftSonIndex;
    }

    public int getRightSonIndex() {
        return rightSonIndex;
    }
}
