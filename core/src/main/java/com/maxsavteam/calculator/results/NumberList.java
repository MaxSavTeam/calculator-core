/*
 * Copyright (C) 2022 MaxSav Team
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

package com.maxsavteam.calculator.results;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NumberList extends BaseResult {

	private final java.util.List<BaseResult> mResults;

	public NumberList(java.util.List<BaseResult> results) {
		mResults = results;
	}

	public List<BaseResult> getResults() {
		return new ArrayList<>(mResults);
	}

	public boolean isSingleNumber() {
		return mResults.size() == 1 && mResults.get(0) instanceof Number;
	}

	public BigDecimal getSingleNumberIfTrue() {
		if (!isSingleNumber())
			throw new IllegalStateException("List does not contain single number");
		return ((Number) mResults.get(0)).get();
	}

	public static NumberList of(BigDecimal a) {
		return new NumberList(
				java.util.List.of(new Number(a))
		);
	}

	public String format(DecimalFormat decimalFormat) {
		if (isSingleNumber()) {
			if (decimalFormat == null)
				return getSingleNumberIfTrue().toPlainString();
			return decimalFormat.format(getSingleNumberIfTrue());
		}
		StringBuilder sb = new StringBuilder("(");
		for (int i = 0; i < mResults.size(); i++) {
			var b = mResults.get(i);
			if (b instanceof NumberList) {
				sb.append(((NumberList) b).format(decimalFormat));
			} else if (b instanceof Number) {
				BigDecimal bd = ((Number) b).get();
				if (decimalFormat != null)
					sb.append(decimalFormat.format(bd));
				else
					sb.append(bd);
			}
			if (i != mResults.size() - 1)
				sb.append("; ");
		}
		sb.append(")");
		return sb.toString();
	}

	public String format() {
		return format(null);
	}

}
