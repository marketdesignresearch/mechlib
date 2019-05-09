package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;

public interface BlockingAllocationFinder {
    BlockingAllocation findBlockingAllocation(Bids bids, AuctionResult priorResult);

}
