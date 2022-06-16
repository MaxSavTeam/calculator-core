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

package com.maxsavteam.calculator;

import java.util.Map;

/**
 * Replaces some special symbols with simple symbols, and then this expression sends to TreeBuilder
 */
public class CalculatorExpressionTokenizer {

	private Map<String, String> mReplacementMap;

	public void setReplacementMap(Map<String, String> replacementMap) {
		mReplacementMap = replacementMap;
	}

	public String tokenizeExpression(String expression) {
		String expr = expression;
		for (Map.Entry<String, String> entry : mReplacementMap.entrySet()) {
			expr = expr.replace(entry.getKey(), entry.getValue());
		}
		return expr;
	}

	public String localizeExpression(String expression) {
		String expr = expression;
		for (Map.Entry<String, String> entry : mReplacementMap.entrySet()) {
			expr = expr.replace(entry.getValue(), entry.getKey());
		}
		return expr;
	}
}
