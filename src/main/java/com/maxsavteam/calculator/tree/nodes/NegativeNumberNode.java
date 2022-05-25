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

import com.maxsavteam.calculator.results.BaseResult;
import com.maxsavteam.calculator.results.NumberList;
import com.maxsavteam.calculator.results.Number;

import java.math.BigDecimal;
import java.util.ArrayList;

public class NegativeNumberNode extends TreeNode {

	private static Number applyOnNum(Number r) {
		return new Number(r.get().multiply(BigDecimal.valueOf(-1)));
	}

	public static NumberList apply(BaseResult r) {
		ArrayList<BaseResult> results = new ArrayList<>();
		if (r instanceof Number) {
			results.add(applyOnNum((Number) r));
		} else {
			NumberList l = (NumberList) r;
			for (BaseResult b : l.getResults()) {
				if (b instanceof NumberList)
					results.add(apply(b));
				else
					results.add(applyOnNum((Number) b));
			}
		}
		return new NumberList(results);
	}

}
