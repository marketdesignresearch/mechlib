package org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Outcome;

public interface ConstraintGenerator {
    /**
     * 
     * @param blockingCoalition
     * @return the ids of the added constraints
     */
    void addConstraint(Allocation blockingCoalition, Outcome priorResult);
}
