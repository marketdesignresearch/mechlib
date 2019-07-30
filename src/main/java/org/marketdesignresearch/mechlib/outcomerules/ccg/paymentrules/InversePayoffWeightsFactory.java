package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.ReferencePointFactory;

public class InversePayoffWeightsFactory implements CorePaymentWeightsFactory {

    @Override
    public CorePaymentWeights createWeights(Outcome referencePoint) {
        return new InversePayoffWeights(referencePoint);
    }

    @Override
    public String getLubinParkesName(Norm norm, ReferencePointFactory referencePoint) {
        String rp = referencePoint.getName();
        if (norm.equals(Norm.EUCLIDEAN)) {
            return rp + "PayoffFractional";
        } else if (norm.equals(Norm.MANHATTAN)) {
            return rp + "PayoffSmall";
        }
        return rp + "InversePayoffWeights";
    }

}
