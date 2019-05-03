package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.Bids;
import org.marketdesignresearch.mechlib.domain.Payment;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.ReferencePointFactory;

public class ReferencePointPayoffWeightsFactory implements CorePaymentWeightsFactory {
    private final ReferencePointFactory rpFactory;

    public ReferencePointPayoffWeightsFactory(ReferencePointFactory rpFactory) {
        this.rpFactory = rpFactory;
    }

    @Override
    public CorePaymentWeights createWeights(AuctionResult referencePoint) {
        Allocation allocation = referencePoint.getAllocation();
        Bids bids = allocation.getBids();
        Payment referencePayments = rpFactory.computeReferencePoint(bids, allocation);
        AuctionResult payoffReferencePoint = new AuctionResult(referencePayments, allocation);
        return new PayoffWeights(payoffReferencePoint);
    }

    @Override
    public String getLubinParkesName(Norm norm, ReferencePointFactory referencePoint) {
        if (norm.equals(Norm.MANHATTAN)) {
            return rpFactory.getName() + "PayoffLarge";
        }
        return rpFactory.getName() + "Payoff";
    }

}
