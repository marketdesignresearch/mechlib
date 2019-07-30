package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.BidsReferencePointFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.ReferencePointFactory;

public class EqualWeightsFactory implements CorePaymentWeightsFactory {

    @Override
    public CorePaymentWeights createWeights(Outcome referencepoint) {
        return CorePaymentWeights.EQUAL_WEIGHTS;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EqualWeightsFactory;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String getLubinParkesName(Norm norm, ReferencePointFactory referncePoint) {
        if (referncePoint instanceof BidsReferencePointFactory && norm.equals(Norm.EUCLIDEAN)) {
            return "NearestBid";
        } else if (referncePoint instanceof BidsReferencePointFactory && norm.equals(Norm.MAXIMUM)) {
            return "Reverse";
        } else if (!referncePoint.belowCore() && norm.equals(Norm.MANHATTAN)) {
            return "L1RP";
        } else if (norm.equals(Norm.MANHATTAN)) {
            return "MRC";
        } else if (norm.equals(Norm.EUCLIDEAN)) {
            return "Quadratic";
        } else if (norm.equals(Norm.ITERATIVE_MAXIMUM)) {
            return "TresholdIterativeMaximum";
        } else if (norm.equals(Norm.MAXIMUM)) {
            return "TresholdMaximum";
        }
        return "EQUAL";
    }
}
