package org.marketdesignresearch.mechlib.outcomerules.vcg;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import lombok.Getter;
import lombok.Setter;

public abstract class VCGRule implements OutcomeRule {

    private Outcome result;

    @Override
    public final Outcome getOutcome() {
        if (result == null) {
            result = calculateVCGPrices();
        }
        return result;
    }

    private Outcome calculateVCGPrices() {
        long start = System.currentTimeMillis();
        WinnerDetermination allocationWdp = getWinnerDetermination();
        allocationWdp.setMipInstrumentation(getMipInstrumentation());
        allocationWdp.setPurpose(MipInstrumentation.MipPurpose.ALLOCATION.name());
        Allocation allocation = allocationWdp.getAllocation();

        Map<Bidder, BidderPayment> payments = new LinkedHashMap<>(allocation.getWinners().size());
        MetaInfo metaInfo = allocation.getMetaInfo();
        if (allocation.getWinners().size() > 0) {
            for (Bidder bidder : allocation.getWinners()) {

                BigDecimal valueWithoutBidder = allocation.getTotalAllocationValue().subtract(allocation.allocationOf(bidder).getValue());
                WinnerDetermination wdWithoutBidder = getWinnerDeterminationWithout(bidder);
                wdWithoutBidder.setMipInstrumentation(getMipInstrumentation());
                wdWithoutBidder.setPurpose(MipInstrumentation.MipPurpose.PAYMENT.name());
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

    // region instrumentation
    @Getter @Setter
    private MipInstrumentation mipInstrumentation = MipInstrumentation.NO_OP;
    // endregion
}
