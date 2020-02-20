package org.marketdesignresearch.mechlib.core.bidder.newstrategy;

import org.marketdesignresearch.mechlib.core.bid.demand.DemandBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DemandQuery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultDemandQueryStrategy implements InteractionStrategy<DemandBid, DemandQuery>{

	private final Bidder bidder;
	
	@Override
	public DemandBid applyStrategy(DemandQuery interaction) {
		return new DemandBid(bidder.getBestBundle(interaction.getPrices()));
	}

}
