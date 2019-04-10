package ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration.PotentialCoalition;
import ch.uzh.ifi.ce.winnerdetermination.ORWinnerDetermination;
import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockingCoalitionDetermination extends ORWinnerDetermination {
    private final Map<Bidder, BigDecimal> previousPayoff = new HashMap<>();
    protected static final String TRAITOR = "TRAITOR_";

    public BlockingCoalitionDetermination(AuctionInstance auctionInstance, AuctionResult previousAuctionResult) {
        super(auctionInstance);
        MIPWrapper mip = getMIP();
        for (Bidder winningBidder : previousAuctionResult.getWinners()) {
            Variable traitor = mip.makeNewBooleanVar(TRAITOR + winningBidder.getId());
            BidderAllocation bidderAllocation = previousAuctionResult.getAllocation().allocationOf(winningBidder);
            BigDecimal payoff = bidderAllocation.getValue().subtract(previousAuctionResult.getPayment().paymentOf(winningBidder).getAmount());
            mip.addObjectiveTerm(payoff.negate().doubleValue(), traitor);

            // traitor is 1 if at least one bundleBid of the bidder was
            // allocated else 0
            for (BundleBid bundleBid : auctionInstance.getBid(winningBidder).getBundleBids()) {

                Variable bidVariable = getBidVariable(bundleBid);

                Constraint bundleAssigned = mip.beginNewLEQConstraint(0.0);
                bundleAssigned.addTerm(1.0, bidVariable);
                bundleAssigned.addTerm(-1, traitor);
                mip.add(bundleAssigned);
            }
            previousPayoff.put(winningBidder, payoff);
        }
    }

    /**
     * The allocation must be corrected by the traitors previous payoff, i.e.
     * their opportunity costs
     */
    @Override
    public Allocation adaptMIPResult(ISolution mipResult) {
        Allocation allocation = super.adaptMIPResult(mipResult);
        Map<Bidder, BidderAllocation> allocations = new HashMap<>(allocation.getTradesMap());
        Set<PotentialCoalition> potentialCoalitions = new HashSet<>();
        for (Bidder bidder : allocation.getWinners()) {
            if (previousPayoff.containsKey(bidder)) {
                BidderAllocation oldBidderAllocation = allocation.allocationOf(bidder);
                BigDecimal tradeValue = oldBidderAllocation.getValue().subtract(previousPayoff.get(bidder));
                BidderAllocation bidderAllocation = new BidderAllocation(tradeValue, oldBidderAllocation.getBundle(), oldBidderAllocation.getAcceptedBids());
                allocations.put(bidder, bidderAllocation);
                potentialCoalitions.add(bidderAllocation.getPotentialCoalition(bidder));
            } else {
                BidderAllocation oldBidderAllocation = allocation.allocationOf(bidder);
                potentialCoalitions.addAll(oldBidderAllocation.getAcceptedBids().stream().map(bundleBid -> bundleBid.getPotentialCoalition(bidder)).collect(Collectors.toList()));
            }
        }
        return new Allocation(allocations, getAuction().getBids(), allocation.getMetaInfo(), potentialCoalitions);
    }

}
