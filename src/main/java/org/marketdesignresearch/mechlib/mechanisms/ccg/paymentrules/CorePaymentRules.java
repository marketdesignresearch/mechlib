package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.core.Payment;

public final class CorePaymentRules {
    private CorePaymentRules() {

    }

    public static ParameterizedCorePaymentRule euclideanMRCRule(MechanismResult referencePoint) {
        CorePaymentNorm primaryObjectiveNorm = new ManhattenNorm(new MechanismResult(Payment.ZERO, referencePoint.getAllocation()), CorePaymentWeights.EQUAL_WEIGHTS);
        CorePaymentNorm norm = new EuclideanNorm(referencePoint, CorePaymentWeights.EQUAL_WEIGHTS);
        return new ParameterizedCorePaymentRule(primaryObjectiveNorm, norm);
    }

    public static CorePaymentRule euclideanMRCRule(MechanismResult referencePoint, double epsilon) {
        return euclideanMRCRule(referencePoint);
    }

    public static CorePaymentRule maximumsMRCRule(MechanismResult referencePoint) {
        CorePaymentNorm primaryObjectiveNorm = new ManhattenNorm(new MechanismResult(Payment.ZERO, referencePoint.getAllocation()), CorePaymentWeights.EQUAL_WEIGHTS);
        CorePaymentNorm norm = new MaximumsNorm(referencePoint);
        return new ParameterizedCorePaymentRule(primaryObjectiveNorm, norm);
    }

    public static CorePaymentRule iterativeMaximumsMRCRule(MechanismResult referencePoint) {
        CorePaymentNorm primaryObjectiveNorm = new ManhattenNorm(new MechanismResult(Payment.ZERO, referencePoint.getAllocation()), CorePaymentWeights.EQUAL_WEIGHTS);
        CorePaymentNorm norm = new IterativeMaximumNorm(referencePoint);
        return new ParameterizedCorePaymentRule(primaryObjectiveNorm, norm);
    }

    public static CorePaymentRule minPaymentRule(Allocation refenceAllocation) {
        MechanismResult referencePoint = new MechanismResult(Payment.ZERO, refenceAllocation);
        CorePaymentNorm norm = new ManhattenNorm(referencePoint, CorePaymentWeights.EQUAL_WEIGHTS);
        return new ParameterizedCorePaymentRule(norm);
    }

    public static CorePaymentRule euclideanEpsilonMRCRule(MechanismResult referencePoint) {
        PaymentNorm manhattenNorm = new ManhattenNorm(referencePoint, CorePaymentWeights.EQUAL_WEIGHTS);
        PaymentNorm euclideanNorm = new EuclideanNorm(referencePoint, CorePaymentWeights.EPSILON_WEIGHTS);
        return new MultiNormCorePaymentRule(referencePoint, manhattenNorm, euclideanNorm);
    }
}
