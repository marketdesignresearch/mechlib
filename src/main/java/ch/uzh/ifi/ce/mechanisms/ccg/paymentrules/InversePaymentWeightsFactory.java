package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.ccg.referencepoint.ReferencePointFactory;

public class InversePaymentWeightsFactory implements CorePaymentWeightsFactory {

    @Override
    public CorePaymentWeights createWeights(AuctionResult referencePoint) {
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
