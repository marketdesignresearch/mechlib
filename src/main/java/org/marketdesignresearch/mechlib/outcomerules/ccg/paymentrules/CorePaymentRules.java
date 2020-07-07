package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;

public final class CorePaymentRules {
	private CorePaymentRules() {

	}

	public static ParameterizedCorePaymentRule euclideanMRCRule(Outcome referencePoint) {
		CorePaymentNorm primaryObjectiveNorm = new ManhattenNorm(
				new Outcome(Payment.ZERO, referencePoint.getAllocation()), CorePaymentWeights.EQUAL_WEIGHTS);
		CorePaymentNorm norm = new EuclideanNorm(referencePoint, CorePaymentWeights.EQUAL_WEIGHTS);
		return new ParameterizedCorePaymentRule(primaryObjectiveNorm, norm);
	}

	public static CorePaymentRule euclideanMRCRule(Outcome referencePoint, double epsilon) {
		return euclideanMRCRule(referencePoint);
	}

	public static CorePaymentRule maximumsMRCRule(Outcome referencePoint) {
		CorePaymentNorm primaryObjectiveNorm = new ManhattenNorm(
				new Outcome(Payment.ZERO, referencePoint.getAllocation()), CorePaymentWeights.EQUAL_WEIGHTS);
		CorePaymentNorm norm = new MaximumsNorm(referencePoint);
		return new ParameterizedCorePaymentRule(primaryObjectiveNorm, norm);
	}

	public static CorePaymentRule iterativeMaximumsMRCRule(Outcome referencePoint) {
		CorePaymentNorm primaryObjectiveNorm = new ManhattenNorm(
				new Outcome(Payment.ZERO, referencePoint.getAllocation()), CorePaymentWeights.EQUAL_WEIGHTS);
		CorePaymentNorm norm = new IterativeMaximumNorm(referencePoint);
		return new ParameterizedCorePaymentRule(primaryObjectiveNorm, norm);
	}

	public static CorePaymentRule minPaymentRule(Allocation refenceAllocation) {
		Outcome referencePoint = new Outcome(Payment.ZERO, refenceAllocation);
		CorePaymentNorm norm = new ManhattenNorm(referencePoint, CorePaymentWeights.EQUAL_WEIGHTS);
		return new ParameterizedCorePaymentRule(norm);
	}

	public static CorePaymentRule euclideanEpsilonMRCRule(Outcome referencePoint) {
		PaymentNorm manhattenNorm = new ManhattenNorm(referencePoint, CorePaymentWeights.EQUAL_WEIGHTS);
		PaymentNorm euclideanNorm = new EuclideanNorm(referencePoint, CorePaymentWeights.EPSILON_WEIGHTS);
		return new MultiNormCorePaymentRule(referencePoint, manhattenNorm, euclideanNorm);
	}
}
