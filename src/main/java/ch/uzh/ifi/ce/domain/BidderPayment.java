package ch.uzh.ifi.ce.domain;

import java.math.BigDecimal;

public class BidderPayment implements Comparable<BidderPayment> {
    public static final BidderPayment ZERO_PAYMENT = new BidderPayment(BigDecimal.ZERO);
    private final BigDecimal amount;

    public BidderPayment(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Payment[amount=" + getAmount() + "]";
    }

    @Override
    public int compareTo(BidderPayment o) {
        return getAmount().compareTo(o.getAmount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BidderPayment that = (BidderPayment) o;
        return amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }
}
