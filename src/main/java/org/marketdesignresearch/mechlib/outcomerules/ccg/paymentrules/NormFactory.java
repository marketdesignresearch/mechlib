package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import java.util.Optional;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.ReferencePointFactory;

public class NormFactory {
    private final Norm norm;
    private final CorePaymentWeightsFactory weightsFactory;
    private final Optional<Payment> fixedPayments;

    public NormFactory(Norm norm, CorePaymentWeightsFactory weightsFactory) {
        this(norm, weightsFactory, null);
    }

    public NormFactory(Norm norm, CorePaymentWeightsFactory weightsFactory, Payment payment) {
        this.norm = norm;
        this.weightsFactory = weightsFactory;
        this.fixedPayments = Optional.ofNullable(payment);
    }

    public CorePaymentNorm getPaymentNorm(Outcome referencePoint) {
        Outcome actualRefencePoint = fixedPayments.map(p -> new Outcome(p, referencePoint.getAllocation())).orElse(referencePoint);
        return norm.asSecondaryNorm(actualRefencePoint, weightsFactory);
    }

    public Norm getNorm() {
        return norm;
    }

    public CorePaymentWeightsFactory getWeightsFactory() {
        return weightsFactory;
    }

    public static NormFactory withEqualWeights(Norm norm) {
        return new NormFactory(norm, new EqualWeightsFactory());
    }

    public Optional<Payment> getFixedPayments() {
        return fixedPayments;
    }

    public String getLubinParkesName(ReferencePointFactory rpFactory) {
        return weightsFactory.getLubinParkesName(norm, rpFactory);
    }

    @Override
    public String toString() {
        return "NormFactory[norm=" + norm + " ,weightsFactory=" + weightsFactory + " ,fixedPayments=" + fixedPayments + "]";
    }
}
