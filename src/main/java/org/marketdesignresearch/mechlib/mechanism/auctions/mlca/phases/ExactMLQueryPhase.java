package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultExactValueQueryInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.MachineLearningComponent;

public class ExactMLQueryPhase extends MLQueryPhase<BundleExactValueBids> {

	public ExactMLQueryPhase(MachineLearningComponent<BundleExactValueBids> mlComponent, long seed) {
		super(mlComponent, seed);
	}

	@Override
	protected AuctionRoundBuilder<BundleExactValueBids> createConcreteAuctionRoundBuilder(
			Auction<BundleExactValueBids> auction, Map<Bidder, Set<Bundle>> restrictedBids,
			Map<UUID, List<ElicitationEconomy>> bidderMarginalsTemp, long nextRandomSeed) {
		return new ExactMLQueryAuctionRoundBuilder(auction,
				auction.getDomain().getBidders().stream()
						.collect(Collectors.toMap(Bidder::getId,
								b -> new DefaultExactValueQueryInteraction(restrictedBids.get(b), b.getId(), auction),
								(e1, e2) -> e1, LinkedHashMap::new)),
				bidderMarginalsTemp, nextRandomSeed);
	}
}
