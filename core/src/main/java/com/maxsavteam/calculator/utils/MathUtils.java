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

import ch.obermuhlner.math.big.BigDecimalMath;
import com.maxsavteam.calculator.exceptions.CalculationException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class MathUtils {
	public static final BigDecimal E = new BigDecimal("2.71828182845904523536");
	public static final BigDecimal PI = new BigDecimal("3.14159265358979323846");
	public static final BigDecimal FI = new BigDecimal("1.61803398874989484820");
	private static final BigDecimal factorialLimit = new BigDecimal("100000");

	private static int HIGH_ROUND_SCALE = 20;

	public static void setHighRoundScale(int highRoundScale) {
		HIGH_ROUND_SCALE = highRoundScale;
	}

	public static int getHighRoundScale() {
		return HIGH_ROUND_SCALE;
	}

	public static BigDecimal exp(BigDecimal x) {
		return BigDecimalMath.exp(x, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal ln(BigDecimal x) {
		if (x.signum() < 0)
			throw new CalculationException(CalculationException.NEGATIVE_PARAMETER_OF_LOG);
		return BigDecimalMath.log(x, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal log(BigDecimal x) {
		if (x.signum() <= 0)
			throw new CalculationException(CalculationException.NEGATIVE_PARAMETER_OF_LOG);
		return BigDecimalMath.log10(x, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal logWithBase(BigDecimal x, BigDecimal base) {
		if (x.signum() <= 0)
			throw new CalculationException(CalculationException.NEGATIVE_PARAMETER_OF_LOG);
		if (base.compareTo(BigDecimal.TEN) == 0)
			return BigDecimalMath.log10(x, new MathContext(HIGH_ROUND_SCALE));
		if (base.compareTo(BigDecimal.valueOf(2)) == 0)
			return BigDecimalMath.log2(x, new MathContext(HIGH_ROUND_SCALE));

		BigDecimal logX = BigDecimalMath.log(x, new MathContext(HIGH_ROUND_SCALE)),
				logB = BigDecimalMath.log(base, new MathContext(HIGH_ROUND_SCALE));
		return logX.divide(logB, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal abs(BigDecimal x) {
		if (x.signum() < 0) {
			return x.multiply(new BigDecimal("-1"));
		} else {
			return x;
		}
	}

	public static BigDecimal gcd(BigDecimal a, BigDecimal b) {
		a = a.abs();
		b = b.abs();
		while (a.signum() != 0 && b.signum() != 0) {
			if (a.compareTo(b) > 0) {
				a = a.remainder(b);
			} else {
				b = b.remainder(a);
			}
		}
		return a.max(b);
	}

	public static BigDecimal gcd(BigDecimal... l) {
		if (l.length < 2) {
			throw new CalculationException(CalculationException.UNKNOWN_CONSTANT, "gcd: expected minimum 2, but found " + l.length);
		}
		BigDecimal r = gcd(l[0], l[1]);
		for (int i = 2; i < l.length; i++)
			r = gcd(r, l[i]);
		return r;
	}

	public static BigDecimal lcm(BigDecimal a, BigDecimal b) {
		a = a.abs();
		b = b.abs();
		return a.multiply(b).divide(gcd(a, b), new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal lcm(BigDecimal... l) {
		if (l.length < 2) {
			throw new CalculationException(CalculationException.UNKNOWN_CONSTANT, "lcm: expected minimum 2, but found " + l.length);
		}
		BigDecimal r = lcm(l[0], l[1]);
		for (int i = 2; i < l.length; i++)
			r = lcm(r, l[i]);
		return r;
	}

	public static BigDecimal tan(BigDecimal x) {
		if(cos(x).setScale(HIGH_ROUND_SCALE, RoundingMode.HALF_UP).signum() == 0)
			throw new CalculationException(CalculationException.INVALID_VALUE_FOR_TANGENT);
		return BigDecimalMath.tan(x, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal arctan(BigDecimal x) {
		return BigDecimalMath.atan(x, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal cot(BigDecimal x) {
		if(sin(x).setScale(HIGH_ROUND_SCALE, RoundingMode.HALF_UP).signum() == 0)
			throw new CalculationException(CalculationException.INVALID_VALUE_FOR_COTANGENT);
		return BigDecimalMath.cot(x, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal arccot(BigDecimal x) {
		return BigDecimalMath.acot(x, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal sin(BigDecimal x) {
		return BigDecimalMath.sin(x, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal arcsin(BigDecimal x) {
		if(x.compareTo(BigDecimal.valueOf(-1)) < 0 || x.compareTo(BigDecimal.ONE) > 0)
			throw new CalculationException(CalculationException.INVALID_ASIN_ACOS_VALUE);
		return BigDecimalMath.asin(x, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal csc(BigDecimal x) {
		BigDecimal sin = sin(x);
		if(sin.setScale(HIGH_ROUND_SCALE, RoundingMode.HALF_UP).signum() == 0)
			throw new CalculationException(CalculationException.INVALID_VALUE_FOR_COSECANT);
		return BigDecimal.ONE.divide(sin, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal arccsc(BigDecimal x){
		if(x.compareTo(BigDecimal.valueOf(-1)) > 0 && x.compareTo(BigDecimal.ONE) < 0)
			throw new CalculationException(CalculationException.INVALID_VALUE_FOR_ASEC_ACSC);
		return arcsin(BigDecimal.ONE.divide(x, new MathContext(HIGH_ROUND_SCALE)));
	}

	public static BigDecimal cos(BigDecimal x) {
		return BigDecimalMath.cos(x, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal arccos(BigDecimal x) {
		if(x.compareTo(BigDecimal.valueOf(-1)) < 0 || x.compareTo(BigDecimal.ONE) > 0)
			throw new CalculationException(CalculationException.INVALID_ASIN_ACOS_VALUE);
		return BigDecimalMath.acos(x, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal sec(BigDecimal x) {
		BigDecimal cos = cos(x);
		if(cos.setScale(HIGH_ROUND_SCALE, RoundingMode.HALF_UP).signum() == 0)
			throw new CalculationException(CalculationException.INVALID_VALUE_FOR_SECANT);
		return BigDecimal.ONE.divide(cos, new MathContext(HIGH_ROUND_SCALE));
	}

	public static BigDecimal arcsec(BigDecimal x) {
		if(x.compareTo(BigDecimal.valueOf(-1)) > 0 && x.compareTo(BigDecimal.ONE) < 0)
			throw new CalculationException(CalculationException.INVALID_VALUE_FOR_ASEC_ACSC);
		return arccos(BigDecimal.ONE.divide(x, new MathContext(HIGH_ROUND_SCALE)));
	}

	public static BigDecimal fact(BigDecimal a, int step) {
		if (a.compareTo(factorialLimit) > 0)
			throw new CalculationException(CalculationException.FACTORIAL_LIMIT_EXCEEDED);
		if (a.signum() == 0)
			return BigDecimal.ONE;
		if (step == 1 && a.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) <= 0)
			return factTree(1L, a.longValue());
		BigDecimal x = a;
		BigDecimal ans = BigDecimal.ONE;
		BigDecimal bStep = BigDecimal.valueOf(step);
		for (; 0 <= x.compareTo(BigDecimal.ONE); x = x.subtract(bStep)) {
			ans = ans.multiply(x);
		}
		return ans;
	}

	private static BigDecimal factTree(long l, long r) {
		if (l > r)
			return BigDecimal.ONE;
		if (l == r)
			return BigDecimal.valueOf(l);
		if (r - l == 1)
			return BigDecimal.valueOf(l * r);
		long m = l + (r - l) / 2;
		return factTree(l, m).multiply(factTree(m + 1, r));
	}

	public static BigDecimal floor(BigDecimal x) {
		String s = x.toPlainString();
		int pos = s.indexOf(".");
		if (pos == -1 || pos == s.length() - 1) {
			return x;
		} else {
			return new BigDecimal(s.substring(0, pos));
		}
	}

	public static BigDecimal ceil(BigDecimal x) {
		String s = x.toPlainString();
		int pos = s.indexOf(".");
		if (pos == -1 || pos == s.length() - 1) {
			return x;
		} else {
			String afterDot = s.substring(pos + 1);
			if (CalculatorUtils.removeZeros(afterDot).equals("0"))
				return new BigDecimal(s.substring(0, pos)); // delete part after dot

			BigDecimal b = abs(new BigDecimal(s.substring(0, pos)));
			b = b.add(BigDecimal.ONE);
			if (s.charAt(0) == '-')
				b = b.multiply(BigDecimal.valueOf(-1));
			return b;
		}
	}

	public static BigDecimal round(BigDecimal x) {
		String s = x.toPlainString();
		int pos = s.indexOf(".");
		if (pos == -1 || pos == s.length() - 1) {
			return x;
		} else {
			String newString = s.substring(0, pos);
			char next = s.charAt(pos + 1);
			if (next >= '0' && next < '5') {
				return new BigDecimal(newString);
			} else {
				BigDecimal b = abs(new BigDecimal(newString));
				b = b.add(BigDecimal.ONE);
				if (newString.charAt(0) == '-')
					b = b.multiply(BigDecimal.valueOf(-1));
				return b;
			}
		}
	}

	public static BigDecimal rootWithBase(BigDecimal a, BigDecimal n) {
		if (a.signum() == 0)
			return BigDecimal.ZERO;
		if (n.remainder(BigDecimal.valueOf(2)).signum() == 0 && a.signum() < 0)
			throw new CalculationException(CalculationException.ROOT_OF_EVEN_DEGREE_OF_NEGATIVE_NUMBER);
		BigDecimal log = ln(a);
		BigDecimal dLog = log.divide(n, new MathContext(HIGH_ROUND_SCALE));
		return exp(dLog);
	}

	public static BigDecimal powWithExp(BigDecimal a, BigDecimal n) {
		BigDecimal ln = ln(a);
		BigDecimal multiplying = n.multiply(ln);
		return exp(multiplying);
	}

	public static BigDecimal pow(BigDecimal a, BigDecimal n) {
		if (a.signum() == 0) {
			if (n.signum() < 0)
				throw new CalculationException(CalculationException.NAN);
			else if (n.signum() == 0)
				throw new CalculationException(CalculationException.UNDEFINED);
			else
				return BigDecimal.ZERO;
		}
		if (n.signum() < 0) {
			BigDecimal result = pow(a, n.multiply(BigDecimal.valueOf(-1)));
			String strRes = BigDecimal.ONE.divide(result, new MathContext(HIGH_ROUND_SCALE)).toPlainString();
			return new BigDecimal(CalculatorUtils.removeZeros(strRes));
		}
		if (Fraction.isFraction(n)) {
			BigDecimal scaledN = n.setScale(3, RoundingMode.HALF_DOWN);
			Fraction fraction = new Fraction(scaledN);
			return MathUtils.rootWithBase(sysPow(a, fraction.getNumerator()), new BigDecimal(fraction.getDenominator()));
		}
		return sysPow(a, n.toBigInteger());
	}

	private static BigDecimal sysPow(BigDecimal a, BigInteger n) {
		if (n.compareTo(BigInteger.ZERO) == 0) {
			return BigDecimal.ONE;
		}
		if (n.remainder(BigInteger.valueOf(2)).compareTo(BigInteger.ONE) == 0) {
			return sysPow(a, n.subtract(BigInteger.ONE)).multiply(a);
		} else {
			BigDecimal b = sysPow(a, n.divide(BigInteger.valueOf(2)));
			return b.multiply(b);
		}
	}

}
