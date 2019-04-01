package ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.winnerdetermination.WinnerDetermination;
import ch.uzh.ifi.ce.utils.PrecisionUtils;

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
    public BlockingAllocation findBlockingAllocation(AuctionInstance auctionInstance, AuctionResult priorResult) {
        AuctionInstance reducedAuction = auctionInstance.reducedBy(priorResult);

        WinnerDetermination blockingCoalitionFinder = new XORBlockingCoalitionDetermination(reducedAuction);
        double lowerBound = priorResult.getPayment().getTotalPayments().subtract(epsilon).doubleValue();
        blockingCoalitionFinder.setLowerBound(lowerBound);
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }

}
