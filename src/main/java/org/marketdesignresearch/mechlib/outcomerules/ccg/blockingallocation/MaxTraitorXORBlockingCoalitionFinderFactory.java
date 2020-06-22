package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class MaxTraitorXORBlockingCoalitionFinderFactory implements BlockingAllocationFinder {

    @Override
    public BlockingAllocation findBlockingAllocation(BundleValueBids<?> bids, Outcome priorResult) {
    	BundleValueBids<?> reducedBids = bids.reducedBy(priorResult);
        WinnerDetermination blockingCoalitionFinder = new XORMaxTraitorBlockingCoalition(reducedBids, priorResult);
        blockingCoalitionFinder.setLowerBound(priorResult.getPayment().getTotalPayments().doubleValue());
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }
}
