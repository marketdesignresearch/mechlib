package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.ReferencePointFactory;

public class PayoffWeightsFactory implements CorePaymentWeightsFactory {

    @Override
    public CorePaymentWeights createWeights(MechanismResult referencePoint) {
        return new PayoffWeights(referencePoint);
    }

    @Override
    public String getLubinParkesName(Norm norm, ReferencePointFactory rpFactory) {
        if (norm.equals(Norm.MANHATTAN)) {
            return rpFactory.getName() + "PayoffLarge";
        }
        return rpFactory.getName() + "PayoffWeights";
    }

}
