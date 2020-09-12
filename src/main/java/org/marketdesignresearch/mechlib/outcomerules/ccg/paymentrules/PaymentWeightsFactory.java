package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.ReferencePointFactory;

public class PaymentWeightsFactory implements CorePaymentWeightsFactory {

	@Override
	public CorePaymentWeights createWeights(Outcome referencePoint) {
		return new PaymentWeights(referencePoint.getPayment());
	}

	@Override
	public String getLubinParkesName(Norm norm, ReferencePointFactory rpFactory) {
		if (norm.equals(Norm.MANHATTAN)) {
			return rpFactory.getName() + "PaymentLarge";
		}
		return rpFactory.getName() + "PaymentWeights";
	}

}
