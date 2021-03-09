package com.maxsavteam.resolvers;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public interface BinaryOperatorResolver {
    @NotNull
    BigDecimal calculate(char operator, BigDecimal a, BigDecimal b);

    @NotNull
    BigDecimal calculatePercent(char binaryOperator, BigDecimal a, BigDecimal percent);
}
