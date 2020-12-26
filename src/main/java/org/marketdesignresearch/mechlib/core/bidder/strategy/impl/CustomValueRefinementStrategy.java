package org.marketdesignresearch.mechlib.core.bidder.strategy.impl;

import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomValueRefinementStrategy extends DefaultRefinementStrategy{

	@Getter
	private final ValueFunction valueFunction;
}
