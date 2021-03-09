package com.maxsavteam.calculator.resolvers;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public interface BracketsResolver {
    @NotNull
    BigDecimal resolve(int bracketType, BigDecimal a);
}
