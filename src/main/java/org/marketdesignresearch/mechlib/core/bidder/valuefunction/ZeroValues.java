package org.marketdesignresearch.mechlib.core.bidder.valuefunction;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.core.Bundle;

public class ZeroValues implements ValueFunction {

	private static final long serialVersionUID = 8139077363593897411L;

	@Override
	public BigDecimal getValue(Bundle bundle) {
		return BigDecimal.ZERO;
	}

}
