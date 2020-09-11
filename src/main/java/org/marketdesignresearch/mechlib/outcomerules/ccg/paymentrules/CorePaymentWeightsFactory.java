package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.ReferencePointFactory;

public interface CorePaymentWeightsFactory {

	CorePaymentWeights createWeights(Outcome referencepoint);

	String getLubinParkesName(Norm norm, ReferencePointFactory referencePoint);
}
