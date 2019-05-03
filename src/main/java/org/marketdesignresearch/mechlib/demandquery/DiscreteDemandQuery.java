package org.marketdesignresearch.mechlib.demandquery;

import org.marketdesignresearch.mechlib.domain.bidder.SimpleBidder;
import org.marketdesignresearch.mechlib.mechanisms.cca.Prices;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.client.SolverClient;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.domain.*;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class DiscreteDemandQuery implements DemandQuery {

    private final Bids bids;

    public DiscreteDemandQuery(Set<SimpleBidder> simpleBidders) {
        this.bids = Bids.fromSimpleBidders(simpleBidders);
    }

    @Override
    public List<BundleBid> getBestBundleBids(String id, Bidder bidder, Prices prices, int numberOfBundles) {
        if (numberOfBundles < 1) {
            return Lists.newArrayList();
        }
        Preconditions.checkArgument(bids.getBidders().contains(bidder));

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
            result.add(new BundleBid(bidderAllocation.getValue(), new Bundle(bidderAllocation.getBundle()), "DQ_" + id + "-" + ++count + "Bidder_" + bidder));
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
