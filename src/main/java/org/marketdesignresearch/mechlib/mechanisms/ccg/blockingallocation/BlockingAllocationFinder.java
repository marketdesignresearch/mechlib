package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;

public interface BlockingAllocationFinder {
    BlockingAllocation findBlockingAllocation(AuctionInstance auctionInstance, AuctionResult priorResult);

}
