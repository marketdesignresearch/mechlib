package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
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
    public BlockingAllocation findBlockingAllocation(AuctionInstance auctionInstance, AuctionResult priorResult) {
        AuctionInstance reducedAuction = auctionInstance.reducedBy(priorResult);

        WinnerDetermination blockingCoalitionFinder = new XORBlockingCoalitionDetermination(reducedAuction);
        double lowerBound = priorResult.getPayment().getTotalPayments().subtract(epsilon).doubleValue();
        blockingCoalitionFinder.setLowerBound(lowerBound);
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }

}
