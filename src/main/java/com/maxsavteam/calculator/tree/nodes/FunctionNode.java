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
