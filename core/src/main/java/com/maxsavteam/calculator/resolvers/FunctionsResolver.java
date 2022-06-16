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

package com.maxsavteam.calculator.resolvers;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * Resolver for all functions
 */
public interface FunctionsResolver {
	/**
	 * @param funcName Name of function to resolve.
	 * @param suffix   Suffix of function (e.g. in expression "sin45" 45 is suffix
	 * @param operand  Result in function's brackets (e.g. "sin(45)" 45 is operand or "sin(45*2)" operand will be 90)
	 */
	BigDecimal resolve(String funcName, @Nullable BigDecimal suffix, BigDecimal operand);
}
