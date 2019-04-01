package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.ccg.referencepoint.ReferencePointFactory;

public class InverseReferencePointPayoffWeightsFactory implements CorePaymentWeightsFactory {
    private final ReferencePointFactory rpFactory;

    public InverseReferencePointPayoffWeightsFactory(ReferencePointFactory rpFactory) {
        this.rpFactory = rpFactory;
    }

    @Override
    public CorePaymentWeights createWeights(AuctionResult referencePoint) {
        Allocation allocation = referencePoint.getAllocation();
        Bids bids = allocation.getBids();
        Payment referencePayments = rpFactory.computeReferencePoint(new AuctionInstance(bids), allocation);
        AuctionResult payoffReferencePoint = new AuctionResult(referencePayments, allocation);
        return new InversePayoffWeights(payoffReferencePoint);
    }

    @Override
    public String getLubinParkesName(Norm norm, ReferencePointFactory referencePoint) {
        String rp = rpFactory.getName();
        if (norm.equals(Norm.EUCLIDEAN)) {
            return rp + "PayoffFractional";
        } else if (norm.equals(Norm.MANHATTAN)) {
            return rp + "PayoffSmall";
        }
        return rp + "InversePayoffWeights";
    }

}
