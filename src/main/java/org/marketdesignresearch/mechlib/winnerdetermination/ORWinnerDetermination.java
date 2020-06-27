package org.marketdesignresearch.mechlib.winnerdetermination;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.winnerdetermination.allocationlimit.OrWDPAllocationLimitUtils;
import org.springframework.data.util.Pair;

import com.google.common.base.Preconditions;

import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;

/**
 * Wraps an OR or OR* winner determination
 * 
 * @author Benedikt Buenz
 * 
 */
public class ORWinnerDetermination extends BidBasedWinnerDetermination {

    protected final MIPWrapper winnerDeterminationProgram;

    public ORWinnerDetermination(BundleValueBids<?> bids) {
        super(bids);
        winnerDeterminationProgram = createWinnerDeterminationMIP(bids);
    }

    protected MIPWrapper createWinnerDeterminationMIP(BundleValueBids<?> bids) {
        MIPWrapper winnerDeterminationProgram = MIPWrapper.makeNewMaxMIP();

        // Add decision variables and objective terms:
        for (Bidder bidder : bids.getBidders()) {
            for (BundleExactValuePair bundleBid : bids.getBid(bidder).getBundleBids()) {
                Variable bidI = winnerDeterminationProgram.makeNewBooleanVar("Bid_" + bundleBid.getId());
                winnerDeterminationProgram.addObjectiveTerm(this.getScaledBundleBidAmount(bundleBid).doubleValue(), bidI);
                bidVariables.put(bundleBid, bidI);
            }
        }
        Map<Good, Constraint> goods = new LinkedHashMap<>();

        for (Bidder bidder : bids.getBidders()) {
            for (BundleExactValuePair bundleBid : bids.getBid(bidder).getBundleBids()) {
                for (BundleEntry entry : bundleBid.getBundle().getBundleEntries()) {
                    Constraint noDoubleAssignment = goods.computeIfAbsent(entry.getGood(), g -> new Constraint(CompareType.LEQ, g.getQuantity()));
                    noDoubleAssignment.addTerm(entry.getAmount(), bidVariables.get(bundleBid));
                }
            }
        }
        goods.values().forEach(winnerDeterminationProgram::add);
        
        // add Allocation Limits
        for (Bidder bidder : bids.getBidders()) {
        	Pair<List<Constraint>, List<Variable>> cv = OrWDPAllocationLimitUtils.PROCESSOR.createVariablesAndConstraints(bidder, bids.getBid(bidder), bidVariables);
        	cv.getFirst().forEach(winnerDeterminationProgram::add);
        	cv.getSecond().forEach(winnerDeterminationProgram::add);
        }

        return winnerDeterminationProgram;
    }

    @Override
    public WinnerDetermination join(WinnerDetermination other) {
        Preconditions.checkArgument(other instanceof BidBasedWinnerDetermination);
        BidBasedWinnerDetermination otherBidBased = (BidBasedWinnerDetermination) other;
        return new ORWinnerDetermination(otherBidBased.getBids().join(getBids()));
    }

    @Override
    public MIPWrapper getMIP() {
        return winnerDeterminationProgram;
    }

}
