package org.marketdesignresearch.mechlib.core.bidder.valuefunction;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.core.Bundle;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FixValue implements ValueFunction{

	private static final long serialVersionUID = -795390697132639260L;

	private final BigDecimal value;
	
	@Override
	public BigDecimal getValue(Bundle bundle) {
		if(bundle.equals(Bundle.EMPTY))
			return BigDecimal.ZERO;
		return this.value;
	}

}
