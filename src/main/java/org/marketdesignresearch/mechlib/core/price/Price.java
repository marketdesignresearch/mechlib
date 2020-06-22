package org.marketdesignresearch.mechlib.core.price;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class Price {
    public static Price ZERO = new Price(BigDecimal.ZERO);

    BigDecimal amount;

    public Price multiply(BigDecimal factor) {
        return new Price(amount.multiply(factor));
    }

    public static Price of(double amount) {
        return new Price(BigDecimal.valueOf(amount));
    }

    public static Price of(int amount) {
        return new Price(BigDecimal.valueOf(amount));
    }
}