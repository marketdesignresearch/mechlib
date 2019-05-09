package org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation;

import edu.harvard.econcs.jopt.solver.ISolution;
import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.BidderAllocation;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration.PotentialCoalition;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class XORBlockingCoalitionDetermination extends XORWinnerDetermination {

    public XORBlockingCoalitionDetermination(Bids bids) {
        super(bids);
    }

    /**
     * The allocation must be corrected by the traitors previous payoff, i.e.
     * their opportunity costs
     */
    @Override
    public Allocation adaptMIPResult(ISolution mipResult) {
        Allocation allocation = super.adaptMIPResult(mipResult);
        Set<PotentialCoalition> potentialCoalitions = new HashSet<>();
        for (Bidder bidder : allocation.getWinners()) {
            BidderAllocation bidderAllocation = allocation.allocationOf(bidder);
            potentialCoalitions.addAll(bidderAllocation.getAcceptedBids().stream().map(acceptedBid -> acceptedBid.getPotentialCoalition(bidder)).collect(Collectors.toList()));
        }
        return new Allocation(allocation.getTradesMap(), allocation.getBids(), allocation.getMetaInfo(), potentialCoalitions);
    }

}
