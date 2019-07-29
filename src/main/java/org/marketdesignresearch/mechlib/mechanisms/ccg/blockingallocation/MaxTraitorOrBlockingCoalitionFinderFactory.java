package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class MaxTraitorOrBlockingCoalitionFinderFactory implements BlockingAllocationFinder {

    @Override
    public BlockingAllocation findBlockingAllocation(Bids bids, MechanismResult priorResult) {
        WinnerDetermination blockingCoalitionFinder = new OrMaxTraitorBlockingCoalitionDetermination(bids, priorResult);
        blockingCoalitionFinder.setLowerBound(priorResult.getPayment().getTotalPayments().subtract(PrecisionUtils.EPSILON).doubleValue());
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }

}
