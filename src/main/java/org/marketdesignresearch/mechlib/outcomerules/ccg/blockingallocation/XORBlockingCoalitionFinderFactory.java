package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import java.math.BigDecimal;

public class XORBlockingCoalitionFinderFactory implements BlockingAllocationFinder {
    /**
     * 
     */
    private final BigDecimal epsilon;

    public XORBlockingCoalitionFinderFactory(BigDecimal epsilon) {
        this.epsilon = epsilon;
    }

    public XORBlockingCoalitionFinderFactory() {
        this.epsilon = PrecisionUtils.EPSILON;
    }

    @Override
    public BlockingAllocation findBlockingAllocation(Bids bids, Outcome priorResult) {
        Bids reducedBids = bids.reducedBy(priorResult);

        WinnerDetermination blockingCoalitionFinder = new XORBlockingCoalitionDetermination(reducedBids);
        double lowerBound = priorResult.getPayment().getTotalPayments().subtract(epsilon).doubleValue();
        blockingCoalitionFinder.setLowerBound(lowerBound);
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }

}
