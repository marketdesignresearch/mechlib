package org.marketdesignresearch.mechlib.outcomerules.vcg;

import lombok.Getter;
import lombok.Setter;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public abstract class VCGRule implements OutcomeRule {
    @Getter
    private MipInstrumentation mipInstrumentation;

    private Outcome result;

    protected VCGRule() {
        this(new MipInstrumentation());
    }

    protected VCGRule(MipInstrumentation mipInstrumentation) {
        this.mipInstrumentation = mipInstrumentation;
    }

    @Override
    public final Outcome getOutcome() {
        if (result == null) {
            result = calculateVCGPrices();
        }
        return result;
    }

    private Outcome calculateVCGPrices() {
        long start = System.currentTimeMillis();

        Allocation allocation = getWinnerDetermination().getAllocation();

        Map<Bidder, BidderPayment> payments = new HashMap<>(allocation.getWinners().size());
        MetaInfo metaInfo = allocation.getMetaInfo();
        if (allocation.getWinners().size() > 0) {
            for (Bidder bidder : allocation.getWinners()) {

                BigDecimal valueWithoutBidder = allocation.getTotalAllocationValue().subtract(allocation.allocationOf(bidder).getValue());
                WinnerDetermination wdWithoutBidder = getWinnerDeterminationWithout(bidder);
                Allocation allocationWithoutBidder = wdWithoutBidder.getAllocation();
                metaInfo = metaInfo.join(allocationWithoutBidder.getMetaInfo());

                BigDecimal valueWDWithoutBidder = allocationWithoutBidder.getTotalAllocationValue();
                BigDecimal paymentAmount = valueWDWithoutBidder.subtract(valueWithoutBidder);
                payments.put(bidder, new BidderPayment(paymentAmount));
            }
        }
        long end = System.currentTimeMillis();
        metaInfo.setJavaRuntime(end - start);
        Payment payment = new Payment(payments, metaInfo);
        return new Outcome(payment, allocation);
    }

    protected abstract WinnerDetermination getWinnerDetermination();
    protected abstract WinnerDetermination getWinnerDeterminationWithout(Bidder bidder);

}
