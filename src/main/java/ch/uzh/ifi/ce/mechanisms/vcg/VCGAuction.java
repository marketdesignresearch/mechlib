package ch.uzh.ifi.ce.mechanisms.vcg;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.AuctionMechanism;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import ch.uzh.ifi.ce.mechanisms.winnerdetermination.WinnerDetermination;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public abstract class VCGAuction implements AuctionMechanism {

    private AuctionResult result;
    private final AuctionInstance auctionInstance;
    private final Allocation allocation;

    public VCGAuction(AuctionInstance auctionInstance, Allocation allocation) {
        this.auctionInstance = auctionInstance;
        this.allocation = allocation;
    }

    public VCGAuction(AuctionInstance auctionInstance) {
        this.auctionInstance = auctionInstance;
        this.allocation = getWinnerDetermination(auctionInstance).getAllocation();
    }

    @Override
    public AuctionResult getAuctionResult() {
        if (result == null) {

            result = calculateVCGPrices();
        }
        return result;
    }

    private AuctionResult calculateVCGPrices() {
        long start = System.currentTimeMillis();

        Map<Bidder, BidderPayment> payments = new HashMap<>(allocation.getWinners().size());
        MetaInfo metaInfo = allocation.getMetaInfo();
        if (auctionInstance.getBidders().size() <= 1) {
            allocation.getWinners().forEach(b -> payments.put(b, new BidderPayment(BigDecimal.ZERO)));
        } else {
            for (Bidder bidder : allocation.getWinners()) {

                BigDecimal valueWithoutBidder = allocation.getTotalAllocationValue().subtract(allocation.allocationOf(bidder).getValue());
                AuctionInstance auctionWithoutBidder = auctionInstance.without(bidder);
                WinnerDetermination wdWithoutBidder = getWinnerDetermination(auctionWithoutBidder);
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

    protected abstract WinnerDetermination getWinnerDetermination(AuctionInstance auctionInstance);

    @Override
    public Allocation getAllocation() {
        return allocation;
    }
}
