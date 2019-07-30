package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class MaxTraitorXORBlockingCoalitionFinderFactory implements BlockingAllocationFinder {

    @Override
    public BlockingAllocation findBlockingAllocation(Bids bids, Outcome priorResult) {
        Bids reducedBids = bids.reducedBy(priorResult);
        WinnerDetermination blockingCoalitionFinder = new XORMaxTraitorBlockingCoalition(reducedBids, priorResult);
        blockingCoalitionFinder.setLowerBound(priorResult.getPayment().getTotalPayments().doubleValue());
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }
}
