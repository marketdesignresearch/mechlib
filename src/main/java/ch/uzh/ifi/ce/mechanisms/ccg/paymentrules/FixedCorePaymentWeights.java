package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.Bidder;

public class FixedCorePaymentWeights implements CorePaymentWeights {
    private final double constant;

    public FixedCorePaymentWeights(double constant) {
        this.constant = constant;
    }

    @Override
    public double getWeight(Bidder bidder) {
        return constant;
    }

}
