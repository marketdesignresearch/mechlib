package ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.AuctionResult;

public interface ConstraintGenerator {
    /**
     * 
     * @param blockingCoalition
     * @return the ids of the added constraints
     */
    void addConstraint(Allocation blockingCoalition, AuctionResult priorResult);
}
