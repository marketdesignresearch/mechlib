package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultExactValueQueryInteraction;
import org.springframework.data.annotation.PersistenceConstructor;

public class ExactRandomQueryPhase extends RandomQueryPhase<BundleExactValueBids>{

	public ExactRandomQueryPhase(long seed) {
		super(seed);
	}
	
	@PersistenceConstructor
	public ExactRandomQueryPhase(long seed, int numberOfQueries) {
		super(seed, numberOfQueries);
	}

	@Override
	protected AuctionRoundBuilder<BundleExactValueBids> createConcreteAuctionRoundBuilder(
			Auction<BundleExactValueBids> auction, Map<Bidder, Set<Bundle>> restrictedBids) {
		return new ExactRandomQueryAuctionRoundBuilder(auction,auction.getDomain().getBidders().stream().collect(Collectors.toMap(b -> b.getId(), b -> new DefaultExactValueQueryInteraction(restrictedBids.get(b), b.getId(), auction),(e1,e2)->e1,LinkedHashMap::new)));
	}

	@Override
	public String getType() {
		return "Exact Random Query Phase";
	}
}
