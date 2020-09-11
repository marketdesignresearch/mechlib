package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.ReferencePointFactory;

public class ReferencePointPaymentsWeightsFactory implements CorePaymentWeightsFactory {
	private final ReferencePointFactory rpFactory;

	public ReferencePointPaymentsWeightsFactory(ReferencePointFactory rpFactory) {
		this.rpFactory = rpFactory;
	}

	@Override
	public CorePaymentWeights createWeights(Outcome referencePoint) {
		Allocation allocation = referencePoint.getAllocation();
		BundleValueBids<?> bids = allocation.getBids();
		Payment referencePayments = rpFactory.computeReferencePoint(bids, allocation);
		return new PaymentWeights(referencePayments);
	}

	@Override
	public String getLubinParkesName(Norm norm, ReferencePointFactory referencePoint) {
		if (norm.equals(Norm.MANHATTAN)) {
			return rpFactory.getName() + "PaymentsLarge";
		}
		return "Payments" + rpFactory.getName();
	}

}
