package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.domain.Bidder;

public class PayoffWeights implements CorePaymentWeights {
    private final AuctionResult referencePoint;

    public PayoffWeights(AuctionResult referencePoint) {
        this.referencePoint = referencePoint;
    }

    @Override
    public double getWeight(Bidder bidder) {

        return referencePoint.payoffOf(bidder).doubleValue();
    }

}
