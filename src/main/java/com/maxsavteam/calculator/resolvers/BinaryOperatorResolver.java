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
