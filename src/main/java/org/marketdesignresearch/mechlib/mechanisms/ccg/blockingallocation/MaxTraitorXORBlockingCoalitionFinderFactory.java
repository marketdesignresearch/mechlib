package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class MaxTraitorXORBlockingCoalitionFinderFactory implements BlockingAllocationFinder {

    @Override
    public BlockingAllocation findBlockingAllocation(AuctionInstance auctionInstance, AuctionResult priorResult) {
        AuctionInstance reducedAuction = auctionInstance.reducedBy(priorResult);
        WinnerDetermination blockingCoalitionFinder = new XORMaxTraitorBlockingCoalition(reducedAuction, priorResult);
        blockingCoalitionFinder.setLowerBound(priorResult.getPayment().getTotalPayments().doubleValue());
        return BlockingAllocation.of(blockingCoalitionFinder.getAllocation());
    }
}
