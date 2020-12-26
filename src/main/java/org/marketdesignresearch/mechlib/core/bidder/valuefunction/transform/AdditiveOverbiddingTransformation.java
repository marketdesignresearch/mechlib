package org.marketdesignresearch.mechlib.core.bidder.valuefunction.transform;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BidTransformableValueFunction;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;

import com.google.common.collect.ImmutableSet;

/**
 * Created by Benedikt on 30.07.15.
 */
public class AdditiveOverbiddingTransformation<S extends ComparableTransformation<S>> implements MultiDimensionalComparableTransformation<AdditiveOverbiddingTransformation<S>> {
    public static final AdditiveOverbiddingTransformation<ShaveTransformation> TRUTHFUL = new AdditiveOverbiddingTransformation<>(ShaveTransformation.TRUTHFUL, BigDecimal.ZERO, ImmutableSet.of());
    private final S normalStrategy;

    private final BigDecimal overBid;
    private final Set<Good> allGoods;

    public AdditiveOverbiddingTransformation(S normalStrategy, BigDecimal overBid, Set<Good> allGoods) {
        this.normalStrategy = normalStrategy;
        this.overBid = overBid;
        this.allGoods = allGoods;
    }


    public Set<Good> getAllGoods() {
        return allGoods;
    }

    public ValueTransformation getNormalStrategy() {
        return normalStrategy;
    }


    @Override
    public BundleExactValueBid apply(BidTransformableValueFunction combinatorialValueFunction) {
    	BundleExactValueBid bid = normalStrategy.apply(combinatorialValueFunction);
        if (overBid.signum() != 0) {
            BigDecimal bidSoFar = bid.getBundleBids().stream().map(BundleExactValuePair::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BundleExactValuePair bundleBid = new BundleExactValuePair(bidSoFar.add(overBid), allGoods, "FakeBig");
            bid.addBundleBid(bundleBid);
        }
        return bid;
    }

    @Override
    public String toString() {
        return "Overbid[" + overBid + "]";
    }

    @Override
    public int dimensions() {
        return 2;
    }



    @Override
    public BigDecimal getExactStrategy(int dimension) {
        if (dimension == 0) {
            return normalStrategy.getExactStrategy();
        } else if (dimension == 1) {
            return overBid;

        }
        throw new IllegalArgumentException("Dimension out of range. 2 dimensions. Requested " + dimension);


    }

    @Override
    public AdditiveOverbiddingTransformation<S> moreTruthful(int dimension, BigDecimal strategyChange) {
        if (dimension == 0) {
            S newNormalStrategy = normalStrategy.moreTruthful(strategyChange);
            return new AdditiveOverbiddingTransformation<>(newNormalStrategy, overBid, allGoods);
        } else if (dimension == 1) {
            BigDecimal newOverBid = overBid.subtract(strategyChange);
            return new AdditiveOverbiddingTransformation<>(normalStrategy, newOverBid, allGoods);

        }
        throw new IllegalArgumentException("Dimension out of range. 2 dimensions. Requested " + dimension);
    }



    @Override
    public AdditiveOverbiddingTransformation<S> merge(AdditiveOverbiddingTransformation<S> other) {
        S mergedNormal = normalStrategy.merge(other.normalStrategy);
        BigDecimal mergedOverbid = overBid.add(other.overBid).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
        return new AdditiveOverbiddingTransformation<>(mergedNormal, mergedOverbid, allGoods);
    }
}