package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RefinementHelper.EfficiencyInfo;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices.LinearPriceGenerator;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;
import org.slf4j.Logger;

public interface BidderRefinementRoundInfoCreator {
	default Map<UUID, BidderRefinementRoundInfo> createBidderRefinementRoundInfos(Auction<BundleBoundValueBids> auction,
			Random random, Map<ElicitationEconomy, EfficiencyInfo> info) {
		Map<UUID, BidderRefinementRoundInfo> previousRoundRefinementInfo = null;

		for (int i = auction.getNumberOfRounds() - 1; i >= 0; i--) {
			AuctionRound<BundleBoundValueBids> auctionRound = auction.getRound(i);
			// Find last RefinementAuctionRound
			if (RefinementAuctionRound.class.isAssignableFrom(auctionRound.getClass())) {
				previousRoundRefinementInfo = ((RefinementAuctionRound) auctionRound).getRefinementInfos();
				break;
			}
		}

		Map<ElicitationEconomy, BidderRefinementRoundInfo> refinementInfos = new LinkedHashMap<>();
		Map<UUID, BidderRefinementRoundInfo> bidderRefinementInfos = new LinkedHashMap<>();

		for (Bidder bidder : auction.getDomain().getBidders()) {
			List<ElicitationEconomy> bidderRefinementEconomies = new ArrayList<>();
			if (previousRoundRefinementInfo != null) {
				bidderRefinementEconomies = previousRoundRefinementInfo.get(bidder.getId()).getEconomiesToRefineNext();
			}
			// Make sure that the refinement conducted in this round is requested by this
			// phase
			bidderRefinementEconomies = bidderRefinementEconomies.stream()
					.filter(b -> this.getRefinementEconomies().contains(b)).collect(Collectors.toList());
			if (bidderRefinementEconomies.isEmpty()) {
				bidderRefinementEconomies = this.getRefinementEconomies(bidder.getId());
			}
			ElicitationEconomy refinementEconomy = bidderRefinementEconomies
					.remove(random.nextInt(bidderRefinementEconomies.size()));

			getLogger().info("Bidder {} uses refinement economy {}", bidder.getName(), refinementEconomy);

			if (!refinementInfos.containsKey(refinementEconomy)) {
				BundleExactValueBids alphaValuation = auction.getLatestAggregatedBids()
						.getAlphaBids(info.get(refinementEconomy).alpha);
				Allocation alphaAllocation = new XORWinnerDetermination(alphaValuation).getAllocation();
				BundleExactValueBids perturbedValuation = auction.getLatestAggregatedBids()
						.getPerturbedBids(alphaAllocation);

				Prices prices = this.getPriceGenerator().getPrices(auction.getDomain(),
						new ElicitationEconomy(auction.getDomain()), alphaAllocation,
						List.of(alphaValuation, perturbedValuation), true);
						//List.of(alphaValuation), false);

				refinementInfos.put(refinementEconomy,
						new BidderRefinementRoundInfo(
								this.createRefinementType(auction.getLatestAggregatedBids()
										.only(new LinkedHashSet<>(refinementEconomy.getBidders()))),
								prices, alphaAllocation, refinementEconomy, bidderRefinementEconomies));
			}
			bidderRefinementInfos.put(bidder.getId(), refinementInfos.get(refinementEconomy));
		}
		return bidderRefinementInfos;
	}

	default List<ElicitationEconomy> getRefinementEconomies(UUID bidder) {
		return this.getRefinementEconomies().stream().filter(e -> e.getBidders().contains(bidder))
				.collect(Collectors.toList());
	}

	LinearPriceGenerator getPriceGenerator();

	Set<RefinementType> createRefinementType(BundleBoundValueBids bids);

	List<ElicitationEconomy> getRefinementEconomies();

	Logger getLogger();
}
