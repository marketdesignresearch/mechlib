package org.marketdesignresearch.mechlib.winnerdetermination;

import edu.harvard.econcs.jopt.solver.mip.CompareType;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIPWrapper;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.BundleBid;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps an XOR winner determination
 * 
 * @author Benedikt Buenz
 * 
 */
public class XORWinnerDetermination extends BidBasedWinnerDetermination {
    // TODO: Make sure we're not running in the same issue as back with SATS with this HashMap
    private final Map<BundleBid, Variable> bidVariables = new HashMap<>();
    private final MIPWrapper winnerDeterminationProgram;

    public XORWinnerDetermination(Bids bids) {
        super(bids);
        winnerDeterminationProgram = createWinnerDeterminationMIP(bids);
    }

    public XORWinnerDetermination(Bids bids, MipInstrumentation.MipPurpose purpose, MipInstrumentation mipInstrumentation) {
        super(bids, purpose, mipInstrumentation);
        winnerDeterminationProgram = createWinnerDeterminationMIP(bids);
    }

    private MIPWrapper createWinnerDeterminationMIP(Bids bids) {
        MIPWrapper winnerDeterminationProgram = MIPWrapper.makeNewMaxMIP();
        // Add decision variables and objective terms:
        Map<Good, Constraint> goods = new HashMap<>();
        for (Bidder bidder : bids.getBidders()) {
            Constraint exclusiveBids = new Constraint(CompareType.LEQ, 1);

            for (BundleBid bundleBid : bids.getBid(bidder).getBundleBids()) {

                Variable bidI = winnerDeterminationProgram.makeNewBooleanVar("Bid_" + bundleBid.getId());
                bidVariables.put(bundleBid, bidI);
                double bidAmount = bundleBid.getAmount().doubleValue();
                winnerDeterminationProgram.addObjectiveTerm(bidAmount, bidI);
                exclusiveBids.addTerm(1, bidI);
                for (BundleEntry entry : bundleBid.getBundle().getBundleEntries()) {
                    Constraint noDoubleAssignment = goods.computeIfAbsent(entry.getGood(), g -> new Constraint(CompareType.LEQ, g.getQuantity()));
                    noDoubleAssignment.addTerm(entry.getAmount(), bidI);
                }
            }
            winnerDeterminationProgram.add(exclusiveBids);
        }

        goods.values().forEach(winnerDeterminationProgram::add);

        return winnerDeterminationProgram;
    }

    @Override
    public MIPWrapper getMIP() {
        return winnerDeterminationProgram;
    }

    @Override
    public Variable getBidVariable(BundleBid bundleBid) {
        return bidVariables.get(bundleBid);
    }

    @Override
    protected Collection<Variable> getBidVariables() {
        return bidVariables.values();
    }
}
