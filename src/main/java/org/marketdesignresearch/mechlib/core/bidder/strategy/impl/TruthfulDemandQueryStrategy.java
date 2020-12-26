package org.marketdesignresearch.mechlib.core.bidder.strategy.impl;

import org.marketdesignresearch.mechlib.core.bid.demand.DemandBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.strategy.DemandQueryStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DemandQuery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class TruthfulDemandQueryStrategy implements DemandQueryStrategy {

	@Setter
	@Getter
	private transient Bidder bidder;

	@Override
	public DemandBid applyDemandStrategy(DemandQuery interaction, Auction<?> auction) {
		return new DemandBid(bidder.getBestBundle(interaction.getPrices()));
	}

}
