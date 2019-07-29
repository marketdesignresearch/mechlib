package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;

public interface CorePaymentWeights {
    double EPSILON = 1e-6;
    CorePaymentWeights EQUAL_WEIGHTS = new FixedCorePaymentWeights(1);
    CorePaymentWeights EPSILON_WEIGHTS = new FixedCorePaymentWeights(EPSILON);

    double getWeight(Bidder bidder);

}
