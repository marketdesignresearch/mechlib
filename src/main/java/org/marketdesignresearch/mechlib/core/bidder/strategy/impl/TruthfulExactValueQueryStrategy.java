package org.marketdesignresearch.mechlib.core.bidder.strategy.impl;

import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;

public class TruthfulExactValueQueryStrategy extends DefaultExactValueStrategy {
	@Override
	public ValueFunction getValueFunction() {
		return this.getBidder();
	}

}
