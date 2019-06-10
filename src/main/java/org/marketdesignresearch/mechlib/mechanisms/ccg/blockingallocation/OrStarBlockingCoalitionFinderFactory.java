package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
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
    public BlockingAllocation findBlockingAllocation(Bids bids, MechanismResult priorResult) {
        WinnerDetermination blockingCoalitionFinder = new BlockingCoalitionDetermination(bids, priorResult);
        blockingCoalitionFinder.setLowerBound(priorResult.getPayment().getTotalPayments().subtract(epsilon).doubleValue());
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }

}
