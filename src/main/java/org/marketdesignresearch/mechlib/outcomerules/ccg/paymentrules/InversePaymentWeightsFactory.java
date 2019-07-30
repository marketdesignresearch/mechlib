package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.ReferencePointFactory;

public class InversePaymentWeightsFactory implements CorePaymentWeightsFactory {

    @Override
    public CorePaymentWeights createWeights(Outcome referencePoint) {
        return new InversePaymentWeights(referencePoint.getAllocation(),referencePoint.getPayment());
    }

    @Override
    public String getLubinParkesName(Norm norm, ReferencePointFactory referencePoint) {
        String rp = referencePoint.getName();
        if (norm.equals(Norm.EUCLIDEAN)) {
            return rp + "PaymentFractional";
        } else if (norm.equals(Norm.MANHATTAN)) {
            return rp + "PaymentSmall";
        }
        return rp + "InversePaymentWeights";
    }

}
