package org.marketdesignresearch.mechlib.core.bidder.strategy.impl;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomValueBoundValueQueryStrategy extends DefaultBoundValueQueryStrategy {
	@Getter
	private ValueFunction valueFunction;

	public CustomValueBoundValueQueryStrategy(ValueFunction valueFunction, BigDecimal stdDeviation) {
		super(stdDeviation);
		this.valueFunction = valueFunction;
	}
}
