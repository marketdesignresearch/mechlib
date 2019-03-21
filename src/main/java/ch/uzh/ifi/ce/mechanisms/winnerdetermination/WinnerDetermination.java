package ch.uzh.ifi.ce.mechanisms.winnerdetermination;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.Allocator;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.math.DoubleMath;
import edu.harvard.econcs.jopt.solver.*;
import edu.harvard.econcs.jopt.solver.client.SolverClient;
import edu.harvard.econcs.jopt.solver.mip.PoolSolution;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class WinnerDetermination implements Allocator {
    private static final Logger LOGGER = LoggerFactory.getLogger(WinnerDetermination.class);
    private Allocation result = null;
    private List<Allocation> intermediateSolutions = null;
    private final AuctionInstance auctionInstance;
    private double lowerBound = 0;

    public WinnerDetermination(AuctionInstance auctionInstance) {
        this.auctionInstance = auctionInstance;
    }

    protected abstract IMIP getMIP();

    @Override
    public Allocation getAllocation() {
        if (result == null) {
            result = solveWinnerDetermination();
        }
        return result;
    }

    protected AuctionInstance getAuction() {
        return auctionInstance;
    }

    protected Allocation solveWinnerDetermination() {
        /*FIXME
        if (auctionInstance.getBidders().isEmpty()) {
            return Allocation.EMPTY_ALLOCATION;
        }*/
        getMIP().setSolveParam(SolveParam.MIN_OBJ_VALUE, lowerBound);
        try {
            IMIPResult mipResult = new SolverClient().solve(getMIP());
            intermediateSolutions = solveIntermediateSolutions(mipResult);
            return adaptMIPResult(mipResult);
        } catch (MIPException ex) {
            LOGGER.warn("WD failed", ex);
            throw new MIPException("MIP infeasible", ex);
        }
    }

    private List<Allocation> solveIntermediateSolutions(IMIPResult result) {
        if (result.getPoolSolutions() != null && !result.getPoolSolutions().isEmpty()) {
            LOGGER.debug("Found {} intermediate solutions candidates", result.getPoolSolutions().size());
            Stream<PoolSolution> blockingStream = result.getPoolSolutions().stream().filter(sol -> sol.getObjectiveValue() > lowerBound)
                    .filter(Predicate.isEqual(result).negate());
            return blockingStream.distinct().map(this::adaptMIPResult).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    protected abstract Variable getBidVariable(BundleBid bundleBid);

    protected Allocation adaptMIPResult(ISolution mipResult) {
        ImmutableMap.Builder<Bidder, BidderAllocation> trades = ImmutableMap.builder();
        for (Bidder bidder : auctionInstance.getBidders()) {
            BigDecimal totalValue = BigDecimal.ZERO;
            Builder<Good> goodsBuilder = ImmutableSet.builder();
            Builder<BundleBid> bundleBids = ImmutableSet.builder();
            for (BundleBid bundleBid : auctionInstance.getBid(bidder).getBundleBids()) {
                if (DoubleMath.fuzzyEquals(mipResult.getValue(getBidVariable(bundleBid)), 1, 1e-3)) {
                    // FIXME: Check if this needs to be adapted to generic quantities. No problem in tests, though..
                    goodsBuilder.addAll(bundleBid.getBundle());
                    bundleBids.add(bundleBid);
                    totalValue = totalValue.add(bundleBid.getAmount());
                }
            }
            Set<Good> goods = goodsBuilder.build();
            if (!goods.isEmpty()) {
                trades.put(bidder, new BidderAllocation(totalValue, goods, bundleBids.build()));
            }
        }

        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setNumberOfMIPs(1);
        metaInfo.setMipSolveTime(mipResult.getSolveTime());
        return new Allocation(trades.build(), auctionInstance.getBids(), metaInfo);
    }

    public List<Allocation> getIntermediateSolutions() {
        getAllocation();
        return intermediateSolutions;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

}