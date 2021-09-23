package org.marketdesignresearch.mechlib.core.bidder.strategy.impl;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TruthfulBoundValueQueryStrategy extends DefaultBoundValueQueryStrategy {

	public TruthfulBoundValueQueryStrategy(BigDecimal stdDeviation) {
		super(stdDeviation);
	}

	@Override
	public ValueFunction getValueFunction() {
		return this.getBidder();
	}
}
