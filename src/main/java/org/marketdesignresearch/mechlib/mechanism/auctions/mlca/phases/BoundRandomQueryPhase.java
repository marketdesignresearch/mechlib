package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultBoundValueQueryInteraction;

public class BoundRandomQueryPhase extends RandomQueryPhase<BundleBoundValueBids> {

	public BoundRandomQueryPhase() {
		super();
	}

	public BoundRandomQueryPhase(int numberOfQueries) {
		super(numberOfQueries);
	}

	@Override
	protected AuctionRoundBuilder<BundleBoundValueBids> createConcreteAuctionRoundBuilder(
			Auction<BundleBoundValueBids> auction, Map<Bidder, Set<Bundle>> restrictedBids) {
		return new BoundRandomQueryAuctionRoundBuilder(auction,
				auction.getDomain().getBidders().stream()
						.collect(Collectors.toMap(b -> b.getId(),
								b -> new DefaultBoundValueQueryInteraction(restrictedBids.get(b), b.getId(), auction),
								(e1, e2) -> e1, LinkedHashMap::new)));
	}

	@Override
	public String getType() {
		return "Bound Random Query Phase";
	}

}
