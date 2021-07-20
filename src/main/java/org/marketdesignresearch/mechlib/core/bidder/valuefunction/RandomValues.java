package org.marketdesignresearch.mechlib.core.bidder.valuefunction;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bidder.random.BidderRandom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RandomValues implements ValueFunction {

	private static final long serialVersionUID = 8783124736356669794L;

	private final BigDecimal lowerBound;
	private final BigDecimal upperBound;

	private Map<Bundle, BigDecimal> drawnValues = new LinkedHashMap<>();

	@Override
	public BigDecimal getValue(Bundle bundle) {
		if (bundle.equals(Bundle.EMPTY)) {
			return BigDecimal.ZERO;
		}
		return drawnValues.computeIfAbsent(bundle, b -> this.drawValue());
	}

	private BigDecimal drawValue() {
		return BigDecimal.valueOf(BidderRandom.INSTANCE.getRandom().nextDouble())
				.multiply(upperBound.subtract(lowerBound)).add(lowerBound);
	}
}
