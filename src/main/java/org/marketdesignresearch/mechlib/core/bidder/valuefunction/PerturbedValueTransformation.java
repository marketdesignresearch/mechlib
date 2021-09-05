package org.marketdesignresearch.mechlib.core.bidder.valuefunction;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bidder.random.BidderRandom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PerturbedValueTransformation implements ValueFunction {

	private static final long serialVersionUID = 4564371817285137223L;

	private final ValueFunction valueFunction;
	private final double stdDeviation;

	private Map<Bundle, BigDecimal> drawnValues = new LinkedHashMap<>();

	@Override
	public BigDecimal getValue(Bundle bundle) {
		if (bundle.equals(Bundle.EMPTY)) {
			return BigDecimal.ZERO;
		}
		return drawnValues.computeIfAbsent(bundle, b -> this.drawnValue(bundle));
	}

	private BigDecimal drawnValue(Bundle bundle) {
		BigDecimal baseValue = valueFunction.getValue(bundle);

		BigDecimal shift = BigDecimal.valueOf(
				baseValue.doubleValue() * BidderRandom.INSTANCE.getRandom().nextGaussian() * this.stdDeviation);

		return baseValue.add(shift).max(BigDecimal.ZERO);
	}

}
