package org.marketdesignresearch.mechlib.outcomerules;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;

public interface OutcomeRule extends PaymentRule, AllocationRule {
	Outcome getOutcome();

	@Override
	default Payment getPayment() {
		return getOutcome().getPayment();
	}

	@Override
	default Allocation getAllocation() {
		return getOutcome().getAllocation();
	}
}
