package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.ccg.referencepoint.ReferencePointFactory;

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
