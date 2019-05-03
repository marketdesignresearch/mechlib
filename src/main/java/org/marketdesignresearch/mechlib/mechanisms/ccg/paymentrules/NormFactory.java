package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.domain.Payment;
import org.marketdesignresearch.mechlib.mechanisms.ccg.referencepoint.ReferencePointFactory;

import java.util.Optional;

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

    public CorePaymentNorm getPaymentNorm(AuctionResult referencePoint) {
        AuctionResult actualRefencePoint = fixedPayments.map(p -> new AuctionResult(p, referencePoint.getAllocation())).orElse(referencePoint);
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
