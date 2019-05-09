package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.ReferencePointFactory;

public class PaymentWeightsFactory implements CorePaymentWeightsFactory {

    @Override
    public CorePaymentWeights createWeights(AuctionResult referencePoint) {
        return new PaymentWeights(referencePoint.getPayment());
    }

    @Override
    public String getLubinParkesName(Norm norm, ReferencePointFactory rpFactory) {
        if (norm.equals(Norm.MANHATTAN)) {
            return rpFactory.getName() + "PaymentLarge";
        }
        return rpFactory.getName() + "PaymentWeights";
    }

}