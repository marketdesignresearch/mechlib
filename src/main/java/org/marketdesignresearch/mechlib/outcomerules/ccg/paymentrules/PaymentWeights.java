package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

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
