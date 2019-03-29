package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation.BlockedBidders;
import ch.uzh.ifi.ce.domain.Payment;
import ch.uzh.ifi.ce.mechanisms.PaymentMechanism;

public interface CorePaymentRule extends PaymentMechanism {

    void resetResult();

    /**
     * 
     * @param blockedBidders
     * @param lastPayment
     * @return null if no constraint was added otherwise the added constraint
     */
    void addBlockingConstraint(BlockedBidders blockedBidders, Payment lastPayment);

}