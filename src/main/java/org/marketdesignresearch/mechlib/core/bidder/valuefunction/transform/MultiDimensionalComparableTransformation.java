package org.marketdesignresearch.mechlib.core.bidder.valuefunction.transform;

import java.math.BigDecimal;

/**
 * Created by Benedikt on 17.08.15.
 */
public interface MultiDimensionalComparableTransformation<S extends MultiDimensionalComparableTransformation<S>> extends ValueTransformation {
    int dimensions();

    default double getStrategyFactor(int dimension) {
        return getExactStrategy(dimension).doubleValue();
    }

    BigDecimal getExactStrategy(int dimension);

    S moreTruthful(int dimension, BigDecimal strategyChange);

    default S lessTruthful(int dimension, BigDecimal strategyChange) {
        return moreTruthful(dimension, strategyChange.negate());
    }

    S merge(S other);
}