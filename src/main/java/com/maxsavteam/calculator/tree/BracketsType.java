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

package com.maxsavteam.calculator.tree;

public class BracketsType {

	public final char openBracket;
	public final char closeBracket;
	public final int type;

	public BracketsType(char openBracket, char closeBracket, int type) {
		this.openBracket = openBracket;
		this.closeBracket = closeBracket;

		if (openBracket == closeBracket)
			throw new IllegalArgumentException("Open and close brackets should not be equal");

		this.type = type;
	}
}
