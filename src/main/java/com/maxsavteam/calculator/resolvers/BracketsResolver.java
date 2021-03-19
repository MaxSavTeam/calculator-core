package com.maxsavteam.calculator.resolvers;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Resolver for different brackets types
 * */
public interface BracketsResolver {
    /**
     * @param bracketType Type of bracket in list of brackets
     * @param a Result in this brackets
     * */
    @NotNull
    BigDecimal resolve(int bracketType, BigDecimal a);
}
