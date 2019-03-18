package ch.uzh.ifi.ce.domain;

import java.math.BigDecimal;
import java.util.Set;

public class AuctionResult {
    private final Payment payment;
    private final Allocation allocation;

    public AuctionResult(Payment payment, Allocation allocation) {
        this.payment = payment;
        this.allocation = allocation;
    }

    public Payment getPayment() {
        return payment;
    }

    public Allocation getAllocation() {
        return allocation;
    }

    public BigDecimal payoffOf(Bidder winner) {
        return allocation.allocationOf(winner).getValue().subtract(payment.paymentOf(winner).getAmount());
    }

    public BigDecimal utilityOf(Bidder bidder, Domain game) {
        BigDecimal value = game.valueOf(bidder, allocation.allocationOf(bidder));
        BigDecimal bidderPayment = payment.paymentOf(bidder).getAmount();
        return value.subtract(bidderPayment);
    }

    public Set<Bidder> getWinners() {
        return allocation.getWinners();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuctionResult)) return false;

        AuctionResult result = (AuctionResult) o;

        if (!payment.equals(result.payment)) return false;
        return allocation.equals(result.allocation);

    }

    @Override
    public int hashCode() {
        int result = payment.hashCode();
        result = 31 * result + allocation.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AuctionResult{" +
                "payment=" + payment +
                ", allocation=" + allocation +
                '}';
    }
}
