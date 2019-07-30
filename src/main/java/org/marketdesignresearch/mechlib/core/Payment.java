package org.marketdesignresearch.mechlib.core;

import com.google.common.collect.Sets;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.marketdesignresearch.mechlib.metainfo.MetaInfoResult;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
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
public final class Payment implements MetaInfoResult {
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

    public Payment merge(Payment other) {
        Map<Bidder, BidderPayment> paymentMap = new HashMap<>();
        for (Bidder bidder : Sets.union(getPaymentMap().keySet(), other.getPaymentMap().keySet())) {
            paymentMap.put(bidder, new BidderPayment(paymentOf(bidder).getAmount().add(other.paymentOf(bidder).getAmount())));
        }
        return new Payment(paymentMap, metaInfo.join(other.metaInfo));
    }
}
