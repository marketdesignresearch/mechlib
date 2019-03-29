package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.Bidder;

public interface CorePaymentWeights {
    double EPSILON = 1e-6;
    CorePaymentWeights EQUAL_WEIGHTS = new FixedCorePaymentWeights(1);
    CorePaymentWeights EPSILON_WEIGHTS = new FixedCorePaymentWeights(EPSILON);

    double getWeight(Bidder bidder);

}
