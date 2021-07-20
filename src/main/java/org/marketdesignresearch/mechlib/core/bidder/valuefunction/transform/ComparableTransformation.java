package org.marketdesignresearch.mechlib.core.bidder.valuefunction.transform;

import java.math.BigDecimal;

public interface ComparableTransformation<S extends ComparableTransformation<S>>
		extends MultiDimensionalComparableTransformation<S>, Comparable<S> {
	default double getStrategyFactor() {
		return getExactStrategy().doubleValue();
	}

	BigDecimal getExactStrategy();

	S moreTruthful(BigDecimal strategyChange);

	default S lessTruthful(BigDecimal strategyChange) {
		return moreTruthful(strategyChange.negate());
	}

	@Override
	default S moreTruthful(int dimension, BigDecimal strategyChange) {
		if (dimension > 0) {
			throw new IllegalArgumentException("Single dimensional strategy. Requested dimension " + dimension);
		}
		return moreTruthful(strategyChange);
	}

	@Override
	default S lessTruthful(int dimension, BigDecimal strategyChange) {
		if (dimension > 0) {
			throw new IllegalArgumentException("Single dimensional strategy. Requested dimension " + dimension);
		}
		return lessTruthful(strategyChange);
	}

	default int dimensions() {
		return 1;
	}

	@Override
	default BigDecimal getExactStrategy(int dimension) {
		if (dimension > 0) {
			throw new IllegalArgumentException("Single dimensional strategy. Requested dimension " + dimension);
		}
		return getExactStrategy();
	}
}
