package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.utils.PrecisionUtils;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import java.math.BigDecimal;

public class OrStarBlockingCoalitionFinderFactory implements BlockingAllocationFinder {
    /**
     * 
     */
    private final BigDecimal epsilon;

    public OrStarBlockingCoalitionFinderFactory(BigDecimal epsilon) {
        this.epsilon = epsilon;
    }

    public OrStarBlockingCoalitionFinderFactory() {
        this.epsilon = PrecisionUtils.EPSILON;
    }

    @Override
    public BlockingAllocation findBlockingAllocation(Bids bids, Outcome priorResult) {
        WinnerDetermination blockingCoalitionFinder = new BlockingCoalitionDetermination(bids, priorResult);
        blockingCoalitionFinder.setLowerBound(priorResult.getPayment().getTotalPayments().subtract(epsilon).doubleValue());
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }

}
