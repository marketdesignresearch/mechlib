package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.outcomerules.PaymentRule;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockedBidders;

public interface CorePaymentRule extends PaymentRule {

	void resetResult();

	/**
	 * 
	 * @param blockedBidders
	 * @param lastPayment
	 * @return null if no constraint was added otherwise the added constraint
	 */
	void addBlockingConstraint(BlockedBidders blockedBidders, Payment lastPayment);

}