package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.ReferencePointFactory;

public interface CorePaymentWeightsFactory {

    CorePaymentWeights createWeights(MechanismResult referencepoint);

    String getLubinParkesName(Norm norm, ReferencePointFactory referencePoint);
}
