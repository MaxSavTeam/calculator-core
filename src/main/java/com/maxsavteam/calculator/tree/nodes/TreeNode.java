/*
 * Copyright (C) 2021 MaxSav Team
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
