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

import java.util.Objects;

public class SuffixOperator {

	private final String symbol;

	public SuffixOperator(char symbol) {
		this(String.valueOf(symbol));
	}

	public SuffixOperator(String symbol){
		if(symbol.length() != 1)
			throw new IllegalArgumentException("Suffix operator must 1 character long");
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SuffixOperator that = (SuffixOperator) o;

		return Objects.equals(symbol, that.symbol);
	}

	@Override
	public int hashCode() {
		return symbol != null ? symbol.hashCode() : 0;
	}
}
