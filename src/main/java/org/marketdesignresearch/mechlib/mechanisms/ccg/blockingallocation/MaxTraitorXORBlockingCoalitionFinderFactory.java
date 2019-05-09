package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class MaxTraitorXORBlockingCoalitionFinderFactory implements BlockingAllocationFinder {

    @Override
    public BlockingAllocation findBlockingAllocation(Bids bids, AuctionResult priorResult) {
        Bids reducedBids = bids.reducedBy(priorResult);
        WinnerDetermination blockingCoalitionFinder = new XORMaxTraitorBlockingCoalition(reducedBids, priorResult);
        blockingCoalitionFinder.setLowerBound(priorResult.getPayment().getTotalPayments().doubleValue());
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }
}
