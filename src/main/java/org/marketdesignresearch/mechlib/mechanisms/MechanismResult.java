package org.marketdesignresearch.mechlib.mechanisms;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.Payment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Set;

@EqualsAndHashCode
@ToString
public final class MechanismResult implements MetaInfoResult {
    public static MechanismResult NONE = new MechanismResult(Payment.ZERO, Allocation.EMPTY_ALLOCATION);
    @Getter
    private final Payment payment;
    @Getter
    private final Allocation allocation;
    @EqualsAndHashCode.Exclude
    @Getter
    private final MetaInfo metaInfo;

    public MechanismResult(Payment payment, Allocation allocation) {
        this.payment = payment;
        this.allocation = allocation;
        this.metaInfo = allocation.getMetaInfo().join(payment.getMetaInfo());
    }

    public BigDecimal payoffOf(Bidder winner) {
        return allocation.allocationOf(winner).getValue().subtract(payment.paymentOf(winner).getAmount());
    }

    public Set<Bidder> getWinners() {
        return allocation.getWinners();
    }

    public MechanismResult merge(MechanismResult other) {
        return new MechanismResult(payment.merge(other.getPayment()), allocation.merge(other.getAllocation()));
    }
}
