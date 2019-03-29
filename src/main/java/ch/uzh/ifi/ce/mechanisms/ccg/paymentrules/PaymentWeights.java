package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.Payment;

public class PaymentWeights implements CorePaymentWeights {
    private final Payment payment;

    public PaymentWeights(Payment payment) {
        this.payment = payment;
    }

    @Override
    public double getWeight(Bidder bidder) {
        return payment.paymentOf(bidder).getAmount().doubleValue();
    }

}
