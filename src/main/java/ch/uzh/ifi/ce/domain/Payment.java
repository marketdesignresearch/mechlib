package ch.uzh.ifi.ce.domain;

import ch.uzh.ifi.ce.mechanisms.MechanismResult;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

/**
 * The payment has exactly the same bidder Set as its corresponding Allocation
 *
 * Map of bidder to payments. One payment per bidder. Payment may
 * be 0 and allocation may of payment may be empty
 * 
 * @author Benedikt
 * 
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = "paymentMap")
@ToString
public final class Payment implements MechanismResult {
    public static final Payment ZERO = new Payment(Collections.emptyMap(), new MetaInfo());

    @Getter
    private final Map<Bidder, BidderPayment> paymentMap;
    @Getter
    private final MetaInfo metaInfo;

    public BigDecimal getTotalPayments() {
        return getPaymentMap().values().stream().map(BidderPayment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BidderPayment paymentOf(Bidder bidder) {
        return paymentMap.getOrDefault(bidder, BidderPayment.ZERO_PAYMENT);
    }

}
