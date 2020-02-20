package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class MaxTraitorOrBlockingCoalitionFinderFactory implements BlockingAllocationFinder {

    @Override
    public BlockingAllocation findBlockingAllocation(BundleValueBids bids, Outcome priorResult) {
        WinnerDetermination blockingCoalitionFinder = new OrMaxTraitorBlockingCoalitionDetermination(bids, priorResult);
        blockingCoalitionFinder.setLowerBound(priorResult.getPayment().getTotalPayments().subtract(PrecisionUtils.EPSILON).doubleValue());
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }

}
