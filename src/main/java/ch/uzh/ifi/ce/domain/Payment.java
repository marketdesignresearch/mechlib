package ch.uzh.ifi.ce.domain;

import ch.uzh.ifi.ce.mechanisms.MechanismResult;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * The payment has exactly the same bidder Set as its corresponding Allocation
 * 
 * @author Benedikt
 * 
 */
public final class Payment implements MechanismResult {
    private final Map<Bidder, BidderPayment> payments;
    private final MetaInfo metaInfo;
    public static final Payment ZERO = new Payment(Collections.emptyMap(), new MetaInfo());

    /**
     * 
     * @param payments
     *            Map of bidder to payments. One payment per bidder. Payment may
     *            be 0 and allocation may of payment may be empty
     */
    public Payment(Map<Bidder, BidderPayment> payments, MetaInfo metaInfo) {
        this.metaInfo = metaInfo;
        this.payments = Collections.unmodifiableMap(payments);
    }

    public BigDecimal getTotalPayments() {
        return getPayments().stream().map(BidderPayment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Collection<BidderPayment> getPayments() {
        return payments.values();
    }

    public Map<Bidder, BidderPayment> getPaymentMap() {
        return payments;
    }

    public BidderPayment paymentOf(Bidder bidder) {
        return payments.getOrDefault(bidder, BidderPayment.ZERO_PAYMENT);
    }

    @Override
    public int hashCode() {
        return payments.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Payment otherPayment = (Payment) obj;
        return payments.equals(otherPayment.payments);
    }

    @Override
    public String toString() {
        return "Payment[payments=" + payments + "\nmetaInfo=" + metaInfo + "]";
    }

    @Override
    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

}
