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
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultExactValueQueryInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.MachineLearningComponent;

/**
 * ML query phase of MLCA that queries exact values (i.e. uses {@link ExactValueQuery}s).
 * @author Manuel Beyeler
 */
public class ExactMLQueryPhase extends MLQueryPhase<BundleExactValueBids> {

	public ExactMLQueryPhase(MachineLearningComponent<BundleExactValueBids> mlComponent) {
		super(mlComponent);
	}

	public ExactMLQueryPhase(MachineLearningComponent<BundleExactValueBids> mlComponent, int maxQueries,
			int numberOfMarginalQueriesPerRound) {
		super(mlComponent, maxQueries, numberOfMarginalQueriesPerRound);
	}

	@Override
	protected AuctionRoundBuilder<BundleExactValueBids> createConcreteAuctionRoundBuilder(
			Auction<BundleExactValueBids> auction, Map<Bidder, Set<Bundle>> restrictedBids,
			Map<UUID, List<ElicitationEconomy>> bidderMarginalsTemp) {
		return new ExactMLQueryAuctionRoundBuilder(auction,
				auction.getDomain().getBidders().stream()
						.collect(Collectors.toMap(Bidder::getId,
								b -> new DefaultExactValueQueryInteraction(restrictedBids.get(b), b.getId(), auction),
								(e1, e2) -> e1, LinkedHashMap::new)),
				bidderMarginalsTemp);
	}

	@Override
	public String getType() {
		return "Exact ML Query Phase";
	}
}
