package com.maxsavteam.calculator.resolvers;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Resolver for suffix operators (e.g. % (percent), ! (factorial))
 * */
public interface SuffixOperatorResolver {
    @NotNull
    BigDecimal resolve(char operator, int count, BigDecimal operand);
}
