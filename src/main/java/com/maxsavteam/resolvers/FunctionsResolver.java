package com.maxsavteam.resolvers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public interface FunctionsResolver {
    @NotNull
    BigDecimal resolve(String funcName, @Nullable BigDecimal suffix, BigDecimal operand);
}
