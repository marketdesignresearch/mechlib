package ch.uzh.ifi.ce.mechanisms.cca;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Price {
    public static Price ZERO = new Price(BigDecimal.ZERO);

    BigDecimal amount;

    public Price multiply(BigDecimal factor) {
        return new Price(amount.multiply(factor));
    }
}
