package com.maxsavteam.calculator.resolvers;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public interface SuffixOperatorResolver {
    @NotNull
    BigDecimal resolve(char operator, int count, BigDecimal operand);
}
