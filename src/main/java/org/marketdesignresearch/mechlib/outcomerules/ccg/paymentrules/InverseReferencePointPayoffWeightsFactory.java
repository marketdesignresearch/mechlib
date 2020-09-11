package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.ReferencePointFactory;

public class InverseReferencePointPayoffWeightsFactory implements CorePaymentWeightsFactory {
	private final ReferencePointFactory rpFactory;

	public InverseReferencePointPayoffWeightsFactory(ReferencePointFactory rpFactory) {
		this.rpFactory = rpFactory;
	}

	@Override
	public CorePaymentWeights createWeights(Outcome referencePoint) {
		Allocation allocation = referencePoint.getAllocation();
		BundleValueBids<?> bids = allocation.getBids();
		Payment referencePayments = rpFactory.computeReferencePoint(bids, allocation);
		Outcome payoffReferencePoint = new Outcome(referencePayments, allocation);
		return new InversePayoffWeights(payoffReferencePoint);
	}

	@Override
	public String getLubinParkesName(Norm norm, ReferencePointFactory referencePoint) {
		String rp = rpFactory.getName();
		if (norm.equals(Norm.EUCLIDEAN)) {
			return rp + "PayoffFractional";
		} else if (norm.equals(Norm.MANHATTAN)) {
			return rp + "PayoffSmall";
		}
		return rp + "InversePayoffWeights";
	}

}
