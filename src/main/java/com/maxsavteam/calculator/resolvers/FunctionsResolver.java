package com.maxsavteam.calculator.resolvers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * Resolver for all functions
 * */
public interface FunctionsResolver {
    /**
     * @param funcName Name of function to resolve.
     * @param suffix Suffix of function (e.g. in expression "sin45" 45 is suffix
     * @param operand Result in brackets of function (e.g. "sin(45)" 45 is operand or "sin(45*2)" operand will be 90
     * */
    @NotNull
    BigDecimal resolve(String funcName, @Nullable BigDecimal suffix, BigDecimal operand);
}
