package org.marketdesignresearch.mechlib.domain.strategy;

import java.math.BigDecimal;

/**
 * Created by Benedikt on 17.08.15.
 */
public interface MultiDimensionalComparableStrategy<S extends MultiDimensionalComparableStrategy<S>> extends Strategy {
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
