package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.ReferencePointFactory;

public class InversePayoffWeightsFactory implements CorePaymentWeightsFactory {

    @Override
    public CorePaymentWeights createWeights(MechanismResult referencePoint) {
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
