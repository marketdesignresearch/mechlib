package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;

public class PayoffWeights implements CorePaymentWeights {
    private final MechanismResult referencePoint;

    public PayoffWeights(MechanismResult referencePoint) {
        this.referencePoint = referencePoint;
    }

    @Override
    public double getWeight(Bidder bidder) {

        return referencePoint.payoffOf(bidder).doubleValue();
    }

}
