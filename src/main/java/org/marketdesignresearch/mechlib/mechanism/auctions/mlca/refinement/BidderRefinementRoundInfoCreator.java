package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement;

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
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RefinementAuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices.LinearPriceGenerator;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BidderRefinementRoundInfoCreator {
	
	@Setter
	@Getter
	private LinearPriceGenerator priceGenerator = new LinearPriceGenerator();
	
	public Map<UUID, BidderRefinementRoundInfo> createBidderRefinementRoundInfos(Auction<BundleBoundValueBids> auction,
			Random random, EfficiencyInfo info) {
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
					.filter(b -> info.getElicitationEconomyEfficiency().keySet().contains(b)).collect(Collectors.toList());
			if (bidderRefinementEconomies.isEmpty()) {
				bidderRefinementEconomies = info.getElicitationEconomyEfficiency().keySet().stream().filter(e -> e.getBidders().contains(bidder.getId()))
						.collect(Collectors.toList());
			}
			ElicitationEconomy refinementEconomy = bidderRefinementEconomies
					.remove(random.nextInt(bidderRefinementEconomies.size()));

			log.info("Bidder {} uses refinement economy {}", bidder.getName(), refinementEconomy);

			if (!refinementInfos.containsKey(refinementEconomy)) {
				BundleExactValueBids alphaValuation = auction.getLatestAggregatedBids()
						.getAlphaBids(info.getElicitationEconomyEfficiency().get(refinementEconomy).alpha);
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
										.only(new LinkedHashSet<>(refinementEconomy.getBidders())), alphaAllocation, prices),
								prices, alphaAllocation, refinementEconomy, bidderRefinementEconomies));
			}
			bidderRefinementInfos.put(bidder.getId(), refinementInfos.get(refinementEconomy));
		}
		return bidderRefinementInfos;
	}

	protected abstract LinkedHashMap<Bidder,LinkedHashSet<RefinementType>> createRefinementType(BundleBoundValueBids bids, Allocation alphaAllocation, Prices pi);
}
