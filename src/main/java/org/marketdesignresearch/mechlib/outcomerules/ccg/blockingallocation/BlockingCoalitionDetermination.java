package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BidderAllocation;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.PotentialCoalition;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;

import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

public class BlockingCoalitionDetermination extends ORWinnerDetermination {
    private final Map<Bidder, BigDecimal> previousPayoff = new LinkedHashMap<>();
    protected static final String TRAITOR = "TRAITOR_";

    public BlockingCoalitionDetermination(BundleValueBids<?> bids, Outcome previousOutcome) {
        super(bids);
        MIPWrapper mip = getMIP();
        for (Bidder winningBidder : previousOutcome.getWinners()) {
            Variable traitor = mip.makeNewBooleanVar(TRAITOR + winningBidder.getId());
            BidderAllocation bidderAllocation = previousOutcome.getAllocation().allocationOf(winningBidder);
            BigDecimal payoff = bidderAllocation.getValue().subtract(previousOutcome.getPayment().paymentOf(winningBidder).getAmount());
            mip.addObjectiveTerm(payoff.negate().multiply(getScalingFactor()).doubleValue(), traitor);

            // traitor is 1 if at least one bundleBid of the bidder was
            // allocated else 0
            for (BundleExactValuePair bundleBid : bids.getBid(winningBidder).getBundleBids()) {

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
        Map<Bidder, BidderAllocation> allocations = new LinkedHashMap<>(allocation.getTradesMap());
        Set<PotentialCoalition> potentialCoalitions = new LinkedHashSet<>();
        for (Bidder bidder : allocation.getWinners()) {
            BidderAllocation oldBidderAllocation = allocation.allocationOf(bidder);
            if (previousPayoff.containsKey(bidder)) {
                BigDecimal tradeValue = oldBidderAllocation.getValue().subtract(previousPayoff.get(bidder));
                BidderAllocation bidderAllocation = new BidderAllocation(tradeValue, oldBidderAllocation.getBundle(), oldBidderAllocation.getAcceptedBids());
                allocations.put(bidder, bidderAllocation);
                potentialCoalitions.add(bidderAllocation.getPotentialCoalition(bidder));
            } else {
                potentialCoalitions.addAll(oldBidderAllocation.getAcceptedBids().stream().map(bundleBid -> bundleBid.getPotentialCoalition(bidder)).collect(Collectors.toList()));
            }
        }
        return new Allocation(allocations, getBids(), allocation.getMetaInfo(), potentialCoalitions);
    }

}
