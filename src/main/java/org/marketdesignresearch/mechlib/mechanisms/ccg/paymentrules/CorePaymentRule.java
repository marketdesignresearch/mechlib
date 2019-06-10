package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.BlockedBidders;
import org.marketdesignresearch.mechlib.domain.Payment;
import org.marketdesignresearch.mechlib.mechanisms.PaymentRule;

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