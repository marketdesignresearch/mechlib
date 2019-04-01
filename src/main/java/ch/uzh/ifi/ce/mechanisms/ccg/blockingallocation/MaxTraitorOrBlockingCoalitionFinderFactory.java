package ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation;

import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.winnerdetermination.WinnerDetermination;
import ch.uzh.ifi.ce.utils.PrecisionUtils;

public class MaxTraitorOrBlockingCoalitionFinderFactory implements BlockingAllocationFinder {

    @Override
    public BlockingAllocation findBlockingAllocation(AuctionInstance auctionInstance, AuctionResult priorResult) {
        WinnerDetermination blockingCoalitionFinder = new OrMaxTraitorBlockingCoalitionDetermination(auctionInstance, priorResult);
        blockingCoalitionFinder.setLowerBound(priorResult.getPayment().getTotalPayments().subtract(PrecisionUtils.EPSILON).doubleValue());
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }

}
