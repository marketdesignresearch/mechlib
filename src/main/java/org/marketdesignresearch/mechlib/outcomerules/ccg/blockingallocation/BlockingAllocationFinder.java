package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Outcome;

public interface BlockingAllocationFinder {
    BlockingAllocation findBlockingAllocation(Bids bids, Outcome priorResult);

}
