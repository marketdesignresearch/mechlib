package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.ReferencePointFactory;

public interface CorePaymentWeightsFactory {

    CorePaymentWeights createWeights(AuctionResult referencepoint);

    String getLubinParkesName(Norm norm, ReferencePointFactory referencePoint);
}
