package org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful;

import org.marketdesignresearch.mechlib.core.bid.demand.DemandBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.DemandQueryStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DemandQuery;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TruthfulDemandQueryStrategy implements DemandQueryStrategy{

	private final Bidder bidder;
	
	@Override
	public DemandBid applyDemandStrategy(DemandQuery interaction) {
		return new DemandBid(bidder.getBestBundle(interaction.getPrices()));
	}

}
