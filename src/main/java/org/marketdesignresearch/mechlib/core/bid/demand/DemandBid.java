package org.marketdesignresearch.mechlib.core.bid.demand;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.Bid;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DemandBid implements Bid{
	
	@Getter
	private final Bundle demandedBundle;
}
