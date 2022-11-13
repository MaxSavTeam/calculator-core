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
import java.math.BigInteger;

public class Fraction {
	private BigInteger numerator; // числитель
	private BigInteger denominator; // знаменатель

	public BigInteger getNumerator() {
		return numerator;
	}

	public BigInteger getDenominator() {
		return denominator;
	}

	private BigInteger gcd(BigInteger a, BigInteger b) {
		while (!a.equals(BigInteger.ZERO) && !b.equals(BigInteger.ZERO)) {
			if (a.compareTo(b) > 0) {
				a = a.remainder(b);
			} else {
				b = b.remainder(a);
			}
		}

		return a.add(b);
	}

	public Fraction(BigDecimal b) {
		String s = b.toPlainString();
		if (isFraction(b)) {
			int pos = s.indexOf(".");
			int n = s.length() - pos - 1;
			this.denominator = BigInteger.valueOf(10).pow(n);
			StringBuilder sb = new StringBuilder(s);
			sb.deleteCharAt(pos);
			this.numerator = new BigInteger(sb.toString());

			BigInteger g = gcd(denominator, numerator);
			while (!g.equals(BigInteger.ONE)) {
				denominator = denominator.divide(g);
				numerator = numerator.divide(g);

				g = gcd(denominator, numerator);
			}
		} else {
			if(s.contains("."))
				s = s.substring(0, s.indexOf("."));
			this.numerator = new BigInteger(s);
			this.denominator = BigInteger.ONE;
		}
	}

	public static boolean isFraction(BigDecimal a) {
		String s = a.toPlainString();
		int pos = s.indexOf('.');
		if (pos == -1)
			return false;
		boolean containsNonZero = false;
		for (int i = pos + 1; i < s.length(); i++) {
			if (s.charAt(i) != '0') {
				containsNonZero = true;
				break;
			}
		}
		return containsNonZero;
	}
}