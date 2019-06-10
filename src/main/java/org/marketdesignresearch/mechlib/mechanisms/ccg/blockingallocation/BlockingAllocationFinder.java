package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;

public interface BlockingAllocationFinder {
    BlockingAllocation findBlockingAllocation(Bids bids, MechanismResult priorResult);

}
