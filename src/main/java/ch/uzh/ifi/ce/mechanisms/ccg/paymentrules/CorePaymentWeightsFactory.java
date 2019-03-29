package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.ccg.referencepoint.ReferencePointFactory;

public interface CorePaymentWeightsFactory {

    CorePaymentWeights createWeights(AuctionResult referencepoint);

    String getLubinParkesName(Norm norm, ReferencePointFactory referencePoint);
}
