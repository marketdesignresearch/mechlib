package org.marketdesignresearch.mechlib.winnerdetermination;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BidderAllocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.math.DoubleMath;

import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.mip.MIP;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class BidBasedWinnerDetermination extends WinnerDetermination {

    private BundleValueBids<?> bids;
    // TODO: Make sure we're not running in the same issue as back with SATS with this HashMap
    protected Map<BundleExactValuePair, Variable> bidVariables = new HashMap<>();
    
    @Getter(AccessLevel.PROTECTED)
    private BigDecimal scalingFactor = BigDecimal.ONE;

    public BidBasedWinnerDetermination(BundleValueBids<?> bids) {
        this.bids = bids;
        
        
        BigDecimal maxValue = bids.getBids().stream().map(BundleValueBid::getBundleBids).flatMap(Set::stream).map(BundleExactValuePair::getAmount).reduce(BigDecimal::max).get();
        BigDecimal maxMipValue = new BigDecimal(MIP.MAX_VALUE).multiply(new BigDecimal(.9));
        
        if (maxValue.compareTo(maxMipValue) > 0) {
            this.scalingFactor = maxMipValue.divide(maxValue, 10, RoundingMode.HALF_UP);
            if (scalingFactor.compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalArgumentException("Bids are are too large, scaling will not make sense because" +
                        "it would result in a very imprecise solution. Scaling factor would be smaller than 1e-10.");
            }
        }
    }
    
    @Override
    public void setLowerBound(double lowerBound) {
    	super.setLowerBound(BigDecimal.valueOf(lowerBound).multiply(this.scalingFactor).doubleValue());
    }
    
    protected BigDecimal getScaledBundleBidAmount(BundleExactValuePair bundleBid) {
    	return bundleBid.getAmount().multiply(this.scalingFactor);
    }

    protected BundleValueBids<?> getBids() {
        return bids;
    }

    @Override
    protected Allocation solveWinnerDetermination() {
        if (bids.getBidders().isEmpty()) {
            return Allocation.EMPTY_ALLOCATION;
        }
        return super.solveWinnerDetermination();
    }

//    @Override
//    public List<Allocation> getBestAllocations(int k) {
//        if (k == 1) return Lists.newArrayList(getAllocation());
//        getMIP().setSolveParam(SolveParam.SOLUTION_POOL_CAPACITY, k);
//        getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE, 4);
//        getMIP().setVariablesOfInterest(getBidVariables());
//        List<Allocation> allocations = getIntermediateSolutions();
//        getMIP().setSolveParam(SolveParam.SOLUTION_POOL_MODE, 0);
//        return allocations;
//    }

    @Override
    public Allocation adaptMIPResult(ISolution mipResult) {
        ImmutableMap.Builder<Bidder, BidderAllocation> trades = ImmutableMap.builder();
        for (Bidder bidder : bids.getBidders()) {
            BigDecimal totalValue = BigDecimal.ZERO;
            HashSet<BundleEntry> bundleEntries = new HashSet<>();
            ImmutableSet.Builder<BundleExactValuePair> bundleBids = ImmutableSet.builder();
            for (BundleExactValuePair bundleBid : bids.getBid(bidder).getBundleBids()) {
            	// An unallocatable bundle might not be added to the mip at all
            	if(getBidVariable(bundleBid) != null) {
            		if (DoubleMath.fuzzyEquals(mipResult.getValue(getBidVariable(bundleBid)), 1, 1e-3)) {
                    	bundleEntries.addAll(bundleBid.getBundle().getBundleEntries());
                    	bundleBids.add(bundleBid);
                    	totalValue = totalValue.add(bundleBid.getAmount());
                	}
            	}
            }
            if (!bundleEntries.isEmpty()) {
                trades.put(bidder, new BidderAllocation(totalValue, new Bundle(bundleEntries), bundleBids.build()));
            }
        }

        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setNumberOfMIPs(1);
        metaInfo.setMipSolveTime(mipResult.getSolveTime());
        return new Allocation(trades.build(), bids, metaInfo);
    }

    protected Variable getBidVariable(BundleExactValuePair bundleBid) {
        return bidVariables.get(bundleBid);
    }

    protected Collection<Variable> getBidVariables() {
        return bidVariables.values();
    }
}