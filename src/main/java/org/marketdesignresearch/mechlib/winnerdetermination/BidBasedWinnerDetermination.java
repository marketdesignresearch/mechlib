package org.marketdesignresearch.mechlib.winnerdetermination;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.math.DoubleMath;
import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanisms.MetaInfo;

import java.math.BigDecimal;
import java.util.Map;

public abstract class BidBasedWinnerDetermination extends WinnerDetermination {

    private final Bids bids;

    public BidBasedWinnerDetermination(Bids bids) {
        this.bids = bids;
    }

    protected Bids getBids() {
        return bids;
    }

    @Override
    protected Allocation solveWinnerDetermination() {
        if (bids.getBidders().isEmpty()) {
            return Allocation.EMPTY_ALLOCATION;
        }
        return super.solveWinnerDetermination();
    }

    @Override
    public Allocation adaptMIPResult(ISolution mipResult) {
        ImmutableMap.Builder<Bidder, BidderAllocation> trades = ImmutableMap.builder();
        for (Bidder bidder : bids.getBidders()) {
            BigDecimal totalValue = BigDecimal.ZERO;
            ImmutableMap.Builder<Good, Integer> goodsBuilder = ImmutableMap.builder();
            ImmutableSet.Builder<BundleBid> bundleBids = ImmutableSet.builder();
            for (BundleBid bundleBid : bids.getBid(bidder).getBundleBids()) {
                if (DoubleMath.fuzzyEquals(mipResult.getValue(getBidVariable(bundleBid)), 1, 1e-3)) {
                    goodsBuilder.putAll(bundleBid.getBundle());
                    bundleBids.add(bundleBid);
                    totalValue = totalValue.add(bundleBid.getAmount());
                }
            }
            Map<Good, Integer> goods = goodsBuilder.build();
            if (!goods.isEmpty()) {
                trades.put(bidder, new BidderAllocation(totalValue, new Bundle(goods), bundleBids.build()));
            }
        }

        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setNumberOfMIPs(1);
        metaInfo.setMipSolveTime(mipResult.getSolveTime());
        return new Allocation(trades.build(), bids, metaInfo);
    }

    protected abstract Variable getBidVariable(BundleBid bundleBid);
}