package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultBoundValueQueryWithMRPARRefinementInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.MachineLearningComponent;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RefinementHelper.RefinementInfo;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices.LinearPriceGenerator;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

public class BoundMLQueryWithMRPARPhase extends MLQueryPhase<BundleBoundValueBids> {

	public BoundMLQueryWithMRPARPhase(MachineLearningComponent<BundleBoundValueBids> mlComponent, long seed) {
		super(mlComponent, seed);
	}

	@Override
	protected AuctionRoundBuilder<BundleBoundValueBids> createConcreteAuctionRoundBuilder(
			Auction<BundleBoundValueBids> auction, Map<Bidder, Set<Bundle>> restrictedBids,
			Map<UUID, List<ElicitationEconomy>> bidderMarginalsTemp, long nextRandomSeed) {

		RefinementInfo info = RefinementHelper.getRefinementInfo(auction);

		BundleBoundValueBids latestAggregatedBids = auction.getLatestAggregatedBids();
		BundleExactValueBids alphaValuation = latestAggregatedBids.getAlphaBids(info.alpha);
		Allocation alphaAllocation = new XORWinnerDetermination(alphaValuation).getAllocation();
		BundleExactValueBids perturbedValuation = latestAggregatedBids.getPerturbedBids(alphaAllocation);

		Prices prices = LinearPriceGenerator.getPrices(auction.getDomain(), new ElicitationEconomy(auction.getDomain()),
				alphaAllocation, List.of(alphaValuation, perturbedValuation), true);

		return new BoundMLQueryWithMRPARAuctionRoundBuilder(auction,
				auction.getDomain().getBidders().stream()
						.collect(Collectors.toMap(Bidder::getId,
								b -> new DefaultBoundValueQueryWithMRPARRefinementInteraction(b.getId(), auction,
										alphaAllocation.allocationOf(b).getBundle(), prices,
										latestAggregatedBids.getBid(b), restrictedBids.get(b)),
								(e1, e2) -> e1, LinkedHashMap::new)),
				bidderMarginalsTemp, nextRandomSeed);
	}
}
