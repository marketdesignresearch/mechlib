package org.marketdesignresearch.mechlib.mechanisms.vcg;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.domain.BidderPayment;
import org.marketdesignresearch.mechlib.domain.Payment;
import org.marketdesignresearch.mechlib.mechanisms.AuctionMechanism;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.MetaInfo;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public abstract class VCGAuction implements AuctionMechanism {

    private AuctionResult result;

    @Override
    public final AuctionResult getAuctionResult() {
        if (result == null) {
            result = calculateVCGPrices();
        }
        return result;
    }

    private AuctionResult calculateVCGPrices() {
        long start = System.currentTimeMillis();

        Allocation allocation = getWinnerDetermination().getAllocation();

        Map<Bidder, BidderPayment> payments = new HashMap<>(allocation.getWinners().size());
        MetaInfo metaInfo = allocation.getMetaInfo();
        if (allocation.getWinners().size() <= 1) {
            allocation.getWinners().forEach(b -> payments.put(b, new BidderPayment(BigDecimal.ZERO)));
        } else {
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
        return new AuctionResult(payment, allocation);
    }

    protected abstract WinnerDetermination getWinnerDetermination();
    protected abstract WinnerDetermination getWinnerDeterminationWithout(Bidder bidder);

}
