package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

public class PayoffWeights implements CorePaymentWeights {
	private final Outcome referencePoint;

	public PayoffWeights(Outcome referencePoint) {
		this.referencePoint = referencePoint;
	}

	@Override
	public double getWeight(Bidder bidder) {

		return referencePoint.payoffOf(bidder).doubleValue();
	}

}
