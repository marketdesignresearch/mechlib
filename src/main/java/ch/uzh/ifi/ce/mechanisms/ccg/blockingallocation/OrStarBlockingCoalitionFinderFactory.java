package ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation;

import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.winnerdetermination.WinnerDetermination;
import ch.uzh.ifi.ce.utils.PrecisionUtils;

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
    public BlockingAllocation findBlockingAllocation(AuctionInstance auctionInstance, AuctionResult priorResult) {
        WinnerDetermination blockingCoalitionFinder = new BlockingCoalitionDetermination(auctionInstance, priorResult);
        blockingCoalitionFinder.setLowerBound(priorResult.getPayment().getTotalPayments().subtract(epsilon).doubleValue());
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }

}
