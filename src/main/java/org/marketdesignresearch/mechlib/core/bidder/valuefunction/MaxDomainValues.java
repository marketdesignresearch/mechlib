package org.marketdesignresearch.mechlib.core.bidder.valuefunction;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MaxDomainValues implements ValueFunction {

	private static final long serialVersionUID = 569673485015832495L;

	private final Domain domain;
	private final BigDecimal epsilon;

	@Override
	public BigDecimal getValue(Bundle bundle) {
		if (bundle.equals(Bundle.EMPTY))
			return BigDecimal.ZERO;
		return domain.getBidders().stream().map(b -> b.getValue(bundle, true).add(epsilon)).max(BigDecimal::compareTo)
				.orElse(BigDecimal.ZERO);
	}
}
