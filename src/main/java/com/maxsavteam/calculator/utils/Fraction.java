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
import java.math.RoundingMode;

public class Fraction {
	private BigDecimal numerator; // числитель
	private BigDecimal denominator; // знаменатель

	public BigDecimal getNumerator() {
		return numerator;
	}

	public BigDecimal getDenominator() {
		return denominator;
	}

	private BigDecimal gcd(BigDecimal f, BigDecimal s){
		BigDecimal a = new BigDecimal( f.toPlainString() );
		BigDecimal b = new BigDecimal( s.toPlainString() );
		while(!a.equals( BigDecimal.ZERO ) && !b.equals( BigDecimal.ZERO )){
			if(a.compareTo(b) > 0){
				a = a.remainder( b );
			}else{
				b = b.remainder( a );
			}
		}

		return a.add(b);
	}

	public Fraction(BigDecimal b){
		String s = b.toPlainString();
		if(s.contains(".")){
			int pos = s.indexOf(".");
			int n = s.length() - pos - 1;
			this.denominator = BigDecimal.valueOf(10).pow(n);
			StringBuilder sb = new StringBuilder(s);
			sb.deleteCharAt(pos);
			this.numerator = new BigDecimal(sb.toString());

			BigDecimal g = gcd( denominator, numerator );
			while(!g.equals( BigDecimal.ONE )){
				denominator = denominator.divide(g, RoundingMode.HALF_EVEN);
				numerator = numerator.divide(g, RoundingMode.HALF_EVEN);

				g = gcd( denominator, numerator );
			}
		}else{
			this.numerator = new BigDecimal(s);
			this.denominator = BigDecimal.ONE;
		}
	}

	public static boolean isFraction(BigDecimal a){
		return a.toPlainString().contains( "." );
	}
}