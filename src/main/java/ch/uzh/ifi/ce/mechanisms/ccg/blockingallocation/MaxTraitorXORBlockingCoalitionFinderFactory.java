package ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation;

import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.winnerdetermination.WinnerDetermination;

public class MaxTraitorXORBlockingCoalitionFinderFactory implements BlockingAllocationFinder {

    @Override
    public BlockingAllocation findBlockingAllocation(AuctionInstance auctionInstance, AuctionResult priorResult) {
        AuctionInstance reducedAuction = auctionInstance.reducedBy(priorResult);
        WinnerDetermination blockingCoalitionFinder = new XORMaxTraitorBlockingCoalition(reducedAuction, priorResult);
        blockingCoalitionFinder.setLowerBound(priorResult.getPayment().getTotalPayments().doubleValue());
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }
}
