package ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation;

import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;

public interface BlockingAllocationFinder {
    BlockingAllocation findBlockingAllocation(AuctionInstance auctionInstance, AuctionResult priorResult);

}
