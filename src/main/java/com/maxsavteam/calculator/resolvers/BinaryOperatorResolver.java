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

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Resolver for all binary operators which {@code Calculator} calculates
 * */
public interface BinaryOperatorResolver {
    @NotNull
    BigDecimal calculate(char operator, BigDecimal a, BigDecimal b);

    /**
     * Calculates percent of number. For example this function will be called for 200+50% to calculate this expression.
     * */
    @NotNull
    BigDecimal calculatePercent(char binaryOperator, BigDecimal a, BigDecimal percent);
}
