package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.Bids;
import org.marketdesignresearch.mechlib.domain.Payment;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.ReferencePointFactory;

public class InverseReferencePointPaymentsWeightsFactory implements CorePaymentWeightsFactory {
    private final ReferencePointFactory rpFactory;

    public InverseReferencePointPaymentsWeightsFactory(ReferencePointFactory rpFactory) {
        this.rpFactory = rpFactory;
    }

    @Override
    public CorePaymentWeights createWeights(AuctionResult referencePoint) {
        Allocation allocation = referencePoint.getAllocation();
        Bids bids = allocation.getBids();
        Payment referencePayments = rpFactory.computeReferencePoint(bids, allocation);
        return new InversePaymentWeights(allocation, referencePayments);
    }

    @Override
    public String getLubinParkesName(Norm norm, ReferencePointFactory referencePoint) {
        if (norm.equals(Norm.EUCLIDEAN)) {
            return rpFactory.getName() + "PaymentsFractional";
        } else if (norm.equals(Norm.MANHATTAN)) {
            return rpFactory.getName() + "PaymentsSmall";
        }
        return "Inverse" + rpFactory.getName();
    }

}
