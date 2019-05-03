package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.domain.Bidder;

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
