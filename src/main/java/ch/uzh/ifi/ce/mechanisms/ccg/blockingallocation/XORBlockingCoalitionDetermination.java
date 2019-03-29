package ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration.PotentialCoalition;
import ch.uzh.ifi.ce.winnerdetermination.XORWinnerDetermination;
import edu.harvard.econcs.jopt.solver.ISolution;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class XORBlockingCoalitionDetermination extends XORWinnerDetermination {

    public XORBlockingCoalitionDetermination(AuctionInstance auctionInstance) {
        super(auctionInstance);
    }

    /**
     * The allocation must be corrected by the traitors previous payoff, i.e.
     * their opportunity costs
     */
    @Override
    protected Allocation adaptMIPResult(ISolution mipResult) {
        Allocation allocation = super.adaptMIPResult(mipResult);
        Set<PotentialCoalition> potentialCoalitions = new HashSet<>();
        for (Bidder bidder : allocation.getWinners()) {
            BidderAllocation bidderAllocation = allocation.allocationOf(bidder);
            potentialCoalitions.addAll(bidderAllocation.getAcceptedBids().stream().map(acceptedBid -> acceptedBid.getPotentialCoalition(bidder)).collect(Collectors.toList()));
        }
        return new Allocation(allocation.getTradesMap(), allocation.getBids(), allocation.getMetaInfo(), potentialCoalitions);
    }

}
