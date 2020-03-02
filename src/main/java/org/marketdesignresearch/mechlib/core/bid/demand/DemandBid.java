package org.marketdesignresearch.mechlib.core.bid.demand;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.Bid;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class DemandBid implements Bid{
	
	@Getter
	private final Bundle demandedBundle;

	@Override
	public boolean isEmpty() {
		return false;
	}
}
