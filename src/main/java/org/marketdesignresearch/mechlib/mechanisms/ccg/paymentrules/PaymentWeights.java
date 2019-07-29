package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.Payment;

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
