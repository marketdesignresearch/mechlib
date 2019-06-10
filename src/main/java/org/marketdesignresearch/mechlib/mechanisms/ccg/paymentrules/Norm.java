package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;

public enum Norm {
    MANHATTAN, EUCLIDEAN, MAXIMUM, ITERATIVE_MAXIMUM;

    public CorePaymentNorm asSecondaryNorm(MechanismResult referencePoint, CorePaymentWeightsFactory weightsFactory) {
        PaymentNorm paymentNorm = getNorm(referencePoint, weightsFactory.createWeights(referencePoint));
        if (paymentNorm instanceof CorePaymentNorm) {
            return (CorePaymentNorm) paymentNorm;
        } else {
            throw new IllegalArgumentException(this + " cannot be used as a c norm");
        }
    }

    private PaymentNorm getNorm(MechanismResult referencePoint, CorePaymentWeights weights) {
        switch (this) {
        case EUCLIDEAN:
            return new EuclideanNorm(referencePoint, weights);
        case ITERATIVE_MAXIMUM:
            return new IterativeMaximumNorm(referencePoint);
        case MANHATTAN:
            return new ManhattenNorm(referencePoint, weights);
        case MAXIMUM:
            return new MaximumsNorm(referencePoint);
        default:
            throw new IllegalArgumentException("Unknown norm " + toString());

        }
    }
}
