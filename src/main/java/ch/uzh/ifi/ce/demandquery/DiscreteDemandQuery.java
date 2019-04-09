package ch.uzh.ifi.ce.demandquery;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.cca.Prices;
import ch.uzh.ifi.ce.winnerdetermination.XORWinnerDetermination;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.client.SolverClient;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class DiscreteDemandQuery implements DemandQuery {

    private Bids bids;

    public DiscreteDemandQuery(Values values) {
        this.bids = values.toBids();
    }

    @Override
    public List<BundleBid> getBestBundleBids(String id, Bidder bidder, Prices prices, int numberOfBundles) {
        if (numberOfBundles < 1) {
            return Lists.newArrayList();
        }

        XORWinnerDetermination xorWinnerDetermination = new XORWinnerDetermination(new AuctionInstance(bids.of(Sets.newHashSet(bidder))));
        xorWinnerDetermination.getMIP().clearObjective();

        for (BundleBid bundleBid : bids.getBid(bidder).getBundleBids()) {
            BigDecimal coefficient = getBidMinusPrice(bundleBid, prices);
            xorWinnerDetermination.getMIP().addObjectiveTerm(coefficient.doubleValue(), xorWinnerDetermination.getBidVariable(bundleBid));
        }

        xorWinnerDetermination.getMIP().setSolveParam(SolveParam.SOLUTION_POOL_CAPACITY, numberOfBundles);
        xorWinnerDetermination.getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE, 4);

        IMIPResult mipResult = new SolverClient().solve(xorWinnerDetermination.getMIP());

        List<BundleBid> result = new ArrayList<>();
        int count = 0;
        for (ISolution solution : mipResult.getPoolSolutions()) {
            Allocation allocation = xorWinnerDetermination.adaptMIPResult(solution);
            BidderAllocation bidderAllocation = allocation.allocationOf(bidder);
            result.add(new BundleBid(bidderAllocation.getValue(), new Bundle(bidderAllocation.getGoodsMap()), "DQ_" + id + "-" + ++count + "Bidder_" + bidder));
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
