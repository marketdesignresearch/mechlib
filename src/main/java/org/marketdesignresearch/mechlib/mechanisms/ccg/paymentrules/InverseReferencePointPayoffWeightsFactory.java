package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.ReferencePointFactory;

public class InverseReferencePointPayoffWeightsFactory implements CorePaymentWeightsFactory {
    private final ReferencePointFactory rpFactory;

    public InverseReferencePointPayoffWeightsFactory(ReferencePointFactory rpFactory) {
        this.rpFactory = rpFactory;
    }

    @Override
    public CorePaymentWeights createWeights(MechanismResult referencePoint) {
        Allocation allocation = referencePoint.getAllocation();
        Bids bids = allocation.getBids();
        Payment referencePayments = rpFactory.computeReferencePoint(bids, allocation);
        MechanismResult payoffReferencePoint = new MechanismResult(referencePayments, allocation);
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
