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

package com.maxsavteam.calculator;

import com.maxsavteam.calculator.utils.MathUtils;

import java.math.BigDecimal;
import java.math.MathContext;

public abstract class AngleUnit {

	protected final MathContext MATH_CONTEXT = new MathContext(MathUtils.getHighRoundScale());

	protected final BigDecimal angle;

	public AngleUnit(BigDecimal angle) {
		this.angle = angle;
	}

	public BigDecimal getAngle() {
		return angle;
	}

	public abstract BigDecimal toDegrees();

	public abstract BigDecimal toRadians();

	public abstract BigDecimal toGradians();

	public static Degree degrees(BigDecimal angle){
		return new Degree(angle);
	}

	public static Radian radians(BigDecimal angle){
		return new Radian(angle);
	}

	public static Gradian gradians(BigDecimal angle){
		return new Gradian(angle);
	}

	public static class Degree extends AngleUnit {
		public Degree(BigDecimal angle) {
			super(angle);
		}

		@Override
		public BigDecimal toDegrees() {
			return angle;
		}

		@Override
		public BigDecimal toRadians() {
			return angle
					.multiply(MathUtils.PI)
					.divide(BigDecimal.valueOf(180), MATH_CONTEXT);
		}

		@Override
		public BigDecimal toGradians() {
			return angle.divide(BigDecimal.valueOf(0.9), MATH_CONTEXT);
		}
	}

	public static class Radian extends AngleUnit {
		public Radian(BigDecimal angle) {
			super(angle);
		}

		@Override
		public BigDecimal toDegrees() {
			return angle
					.multiply(BigDecimal.valueOf(180))
					.divide(MathUtils.PI, MATH_CONTEXT);
		}

		@Override
		public BigDecimal toRadians() {
			return angle;
		}

		@Override
		public BigDecimal toGradians() {
			return angle
					.multiply(BigDecimal.valueOf(200))
					.divide(MathUtils.PI, MATH_CONTEXT);
		}
	}

	public static class Gradian extends AngleUnit {
		public Gradian(BigDecimal angle) {
			super(angle);
		}

		@Override
		public BigDecimal toDegrees() {
			return angle.multiply(BigDecimal.valueOf(0.9));
		}

		@Override
		public BigDecimal toRadians() {
			return angle
					.multiply(MathUtils.PI)
					.divide(BigDecimal.valueOf(200), MATH_CONTEXT);
		}

		@Override
		public BigDecimal toGradians() {
			return angle;
		}
	}

}
