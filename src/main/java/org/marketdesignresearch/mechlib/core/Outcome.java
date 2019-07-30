package org.marketdesignresearch.mechlib.core;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.metainfo.MetaInfoResult;

import java.math.BigDecimal;
import java.util.Set;

@EqualsAndHashCode
@ToString
public final class Outcome implements MetaInfoResult {
    public static Outcome NONE = new Outcome(Payment.ZERO, Allocation.EMPTY_ALLOCATION);
    @Getter
    private final Payment payment;
    @Getter
    private final Allocation allocation;
    @EqualsAndHashCode.Exclude
    @Getter
    private final MetaInfo metaInfo;

    public Outcome(Payment payment, Allocation allocation) {
        this.payment = payment;
        this.allocation = allocation;
        this.metaInfo = allocation.getMetaInfo().join(payment.getMetaInfo());
    }

    public BigDecimal payoffOf(Bidder winner) {
        return allocation.allocationOf(winner).getValue().subtract(payment.paymentOf(winner).getAmount());
    }

    public Set<? extends Bidder> getWinners() {
        return allocation.getWinners();
    }

    public Outcome merge(Outcome other) {
        return new Outcome(payment.merge(other.getPayment()), allocation.merge(other.getAllocation()));
    }
}
