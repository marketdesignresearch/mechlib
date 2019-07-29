package org.marketdesignresearch.mechlib.mechanisms.vcg;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.mechanisms.OutputRule;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.MetaInfo;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public abstract class VCGMechanism implements OutputRule {

    private MechanismResult result;

    @Override
    public final MechanismResult getMechanismResult() {
        if (result == null) {
            result = calculateVCGPrices();
        }
        return result;
    }

    private MechanismResult calculateVCGPrices() {
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
        return new MechanismResult(payment, allocation);
    }

    protected abstract WinnerDetermination getWinnerDetermination();
    protected abstract WinnerDetermination getWinnerDeterminationWithout(Bidder bidder);

}
