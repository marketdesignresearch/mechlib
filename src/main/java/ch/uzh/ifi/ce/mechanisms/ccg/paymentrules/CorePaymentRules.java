package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.domain.Payment;

public final class CorePaymentRules {
    private CorePaymentRules() {

    }

    public static ParameterizedCorePaymentRule euclideanMRCRule(AuctionResult referencePoint) {
        CorePaymentNorm primaryObjectiveNorm = new ManhattenNorm(new AuctionResult(Payment.ZERO, referencePoint.getAllocation()), CorePaymentWeights.EQUAL_WEIGHTS);
        CorePaymentNorm norm = new EuclideanNorm(referencePoint, CorePaymentWeights.EQUAL_WEIGHTS);
        return new ParameterizedCorePaymentRule(primaryObjectiveNorm, norm);
    }

    public static CorePaymentRule euclideanMRCRule(AuctionResult referencePoint, double epsilon) {
        return euclideanMRCRule(referencePoint);
    }

    public static CorePaymentRule maximumsMRCRule(AuctionResult referencePoint) {
        CorePaymentNorm primaryObjectiveNorm = new ManhattenNorm(new AuctionResult(Payment.ZERO, referencePoint.getAllocation()), CorePaymentWeights.EQUAL_WEIGHTS);
        CorePaymentNorm norm = new MaximumsNorm(referencePoint);
        return new ParameterizedCorePaymentRule(primaryObjectiveNorm, norm);
    }

    public static CorePaymentRule iterativeMaximumsMRCRule(AuctionResult referencePoint) {
        CorePaymentNorm primaryObjectiveNorm = new ManhattenNorm(new AuctionResult(Payment.ZERO, referencePoint.getAllocation()), CorePaymentWeights.EQUAL_WEIGHTS);
        CorePaymentNorm norm = new IterativeMaximumNorm(referencePoint);
        return new ParameterizedCorePaymentRule(primaryObjectiveNorm, norm);
    }

    public static CorePaymentRule minPaymentRule(Allocation refenceAllocation) {
        AuctionResult referencePoint = new AuctionResult(Payment.ZERO, refenceAllocation);
        CorePaymentNorm norm = new ManhattenNorm(referencePoint, CorePaymentWeights.EQUAL_WEIGHTS);
        return new ParameterizedCorePaymentRule(norm);
    }

    public static CorePaymentRule euclideanEpsilonMRCRule(AuctionResult referencePoint) {
        PaymentNorm manhattenNorm = new ManhattenNorm(referencePoint, CorePaymentWeights.EQUAL_WEIGHTS);
        PaymentNorm euclideanNorm = new EuclideanNorm(referencePoint, CorePaymentWeights.EPSILON_WEIGHTS);
        return new MultiNormCorePaymentRule(referencePoint, manhattenNorm, euclideanNorm);
    }
}
