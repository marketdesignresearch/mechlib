package ch.uzh.ifi.ce.demandquery;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.cca.Prices;
import ch.uzh.ifi.ce.winnerdetermination.XORWinnerDetermination;
import com.google.common.collect.Lists;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.client.SolverClient;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class DiscreteDemandQuery extends XORWinnerDetermination implements DemandQuery {

    private Bids bids;

    public DiscreteDemandQuery(Values values) {
        super(new AuctionInstance(values.toBids()));
        this.bids = values.toBids();
    }

    @Override
    public BundleBid getBundleBid(Bidder bidder, Prices prices) {
        List<BundleBid> results = getBestBundleBids(bidder, prices, 1);
        if (results.size() > 1) log.warn("Requested one solution, got {}.", results.size());
        return results.get(0);
    }

    @Override
    public List<BundleBid> getBestBundleBids(Bidder bidder, Prices prices, int numberOfBundles) {
        if (numberOfBundles < 1) {
            return Lists.newArrayList();
        }

        IMIP mip = getMIP().typedClone();
        mip.clearObjective();

        Set<Variable> variablesOfInterest = new HashSet<>();

        for (BundleBid bundleBid : bids.getBid(bidder).getBundleBids()) {
            variablesOfInterest.add(getBidVariable(bundleBid));
            BigDecimal coefficient = getBidMinusPrice(bundleBid, prices);
            mip.addObjectiveTerm(coefficient.doubleValue(), getBidVariable(bundleBid));
        }

        mip.setSolveParam(SolveParam.SOLUTION_POOL_CAPACITY, numberOfBundles);
        mip.setSolveParam(SolveParam.SOLUTION_POOL_MODE, 4);
        mip.setVariablesOfInterest(variablesOfInterest);

        IMIPResult mipResult = new SolverClient().solve(mip);

        List<BundleBid> result = new ArrayList<>();
        for (ISolution solution : mipResult.getPoolSolutions()) {
            Allocation allocation = adaptMIPResult(solution);
            BidderAllocation bidderAllocation = allocation.allocationOf(bidder);
            BigDecimal value = bidderAllocation.getAcceptedBids().stream()
                    .map(bid -> getBidMinusPrice(bid, prices))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.add(new BundleBid(value, new Bundle(bidderAllocation.getGoodsMap()), "DQ_Bidder_" + bidder + "_" + prices));
        }
        return result;
    }

    private BigDecimal getBidMinusPrice(BundleBid bundleBid, Prices prices) {
        BigDecimal result = bundleBid.getAmount();
        for (Map.Entry<Good, Integer> entry : bundleBid.getBundle().entrySet()) {
            BigDecimal cost = prices.get(entry.getKey()).getAmount().multiply(BigDecimal.valueOf(entry.getValue()));
            result = result.subtract(cost);
        }
        return result;
    }
}
