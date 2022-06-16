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

package com.maxsavteam.calculator.utils;

import java.math.BigDecimal;
import java.util.List;

public class CalculatorUtils {

	public static <T> void requireUniqueItems(List<T> list){
		if(list.stream().distinct().count() != list.size())
			throw new IllegalArgumentException("List contains identical items");
	}

	public static String removeZeros(String s) {
		String res = s;
		int len = res.length();
		if (res.contains(".") && res.charAt(len - 1) == '0') {
			while (res.charAt(len - 1) == '0') {
				len--;
				res = res.substring(0, len);
			}
			if (res.charAt(len - 1) == '.') {
				res = res.substring(0, len - 1);
			}
		}
		while (res.charAt(0) == '0' && res.length() > 1 && res.charAt(1) != '.') {
			res = res.substring(1);
		}
		return res;
	}

	public static BigDecimal removeZeros(BigDecimal b) {
		return new BigDecimal(removeZeros(b.toPlainString()));
	}

	public static String removeSpaces(String ex) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ex.length(); i++) {
			if (ex.charAt(i) != ' ')
				sb.append(ex.charAt(i));
		}
		return sb.toString();
	}

	public static boolean isLetter(char c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
	}

	public static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
}
