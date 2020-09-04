package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.MachineLearningAllocationInferrer;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.MachineLearningComponent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class MLQueryPhase<T extends BundleValueBids<?>> implements AuctionPhase<T> {

	private static final int DEFAULT_NUMBER_OF_MARGINAL_QUERIES_PER_ROUND = 5;
	private static final int DEFAULT_MAXIMAL_NUMBER_OF_TOTAL_QUERIES = 100;

	private final MachineLearningComponent<T> machineLearningComponent;
	@Getter
	private final int maxQueries;
	@Getter
	private final int numberOfMarginalQueriesPerRound;
	private ElicitationEconomy mainEconomy;
	private List<ElicitationEconomy> marginalEconomies;

	public MLQueryPhase(MachineLearningComponent<T> mlComponent) {
		this(mlComponent, DEFAULT_MAXIMAL_NUMBER_OF_TOTAL_QUERIES, DEFAULT_NUMBER_OF_MARGINAL_QUERIES_PER_ROUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public AuctionRoundBuilder<T> createNextRoundBuilder(Auction<T> auction) {
		if (mainEconomy == null) {
			this.mainEconomy = new ElicitationEconomy(auction.getDomain());
			this.marginalEconomies = new ArrayList<>();
			auction.getDomain().getBidders()
					.forEach(bidder -> this.marginalEconomies.add(new ElicitationEconomy(auction.getDomain(), bidder)));
		}

		Map<Bidder, Set<Bundle>> restrictedBids = new LinkedHashMap<>();
		Map<UUID, List<ElicitationEconomy>> bidderMarginalsTemp = new LinkedHashMap<>();

		for (int i = auction.getNumberOfRounds() - 1; i >= 0; i--) {
			AuctionRound<T> auctionRound = auction.getRound(i);
			// Find last MLQueryAuctionRound
			if (MLQueryAuctionRound.class.isAssignableFrom(auctionRound.getClass())) {
				bidderMarginalsTemp = ((MLQueryAuctionRound) auctionRound).getMarginalsToQueryNext();
				break;
			}
		}

		MachineLearningAllocationInferrer mlai = this.machineLearningComponent
				.getMLFunction(auction.getLatestAggregatedBids());

		int currentNumberOfQueries = auction.getMaximumSubmittedBids();
		int marginalQueries = currentNumberOfQueries + this.numberOfMarginalQueriesPerRound < this.getMaxQueries()
				? this.numberOfMarginalQueriesPerRound
				: this.getMaxQueries() - currentNumberOfQueries - 1;

		// Marginal Elicitation
		for (Bidder bidder : auction.getDomain().getBidders()) {
			log.info("Add marginal queries for Bidder {}", bidder.getName());

			bidderMarginalsTemp.putIfAbsent(bidder.getId(), new ArrayList<>());
			restrictedBids.putIfAbsent(bidder, new LinkedHashSet<>());

			for (int i = 0; i < marginalQueries; i++) {
				// If all marginals were processed refill marginal list
				if (bidderMarginalsTemp.get(bidder.getId()).isEmpty())
					bidderMarginalsTemp.put(bidder.getId(), this.marginalEconomies.stream()
							.filter(me -> me.getBidders().contains(bidder.getId())).collect(Collectors.toList()));

				// choose random setting and remove from setting list
				ElicitationEconomy economy = bidderMarginalsTemp.get(bidder.getId()).remove(
						auction.getCurrentRoundRandom().nextInt(bidderMarginalsTemp.get(bidder.getId()).size()));

				Allocation inferredEfficientAllocation = mlai
						.getInferredEfficientAllocation(auction.getDomain(), economy,
								Map.of(bidder, Stream.concat(
												auction.getLatestAggregatedBids().getBid(bidder).getBundleBids()
														.stream().map(bb -> bb.getBundle()),
												restrictedBids.get(bidder).stream())
										.collect(Collectors.toCollection(LinkedHashSet::new))));
				log.info(economy.toString() + " New bundle: "
						+ inferredEfficientAllocation.getTradesMap().get(bidder).getBundle());
				restrictedBids.get(bidder).add(inferredEfficientAllocation.getTradesMap().get(bidder).getBundle());
			}
		}

		// Main Elicitation
		for (Bidder bidder : auction.getDomain().getBidders()) {
			Allocation infAllocation = mlai
					.getInferredEfficientAllocation(
							auction.getDomain(), this.mainEconomy, Map
									.of(bidder, Stream
											.concat(Stream.concat(
													auction.getLatestAggregatedBids().getBid(bidder).getBundleBids()
															.stream().map(bb -> bb.getBundle()),
													restrictedBids.get(bidder).stream()), Stream.of(Bundle.EMPTY))
											.collect(Collectors.toCollection(LinkedHashSet::new))));
			log.info("Bidder {}: Main Economy New bundle: {}", bidder.getName(),
					infAllocation.getTradesMap().get(bidder).getBundle());
			restrictedBids.get(bidder).add(infAllocation.getTradesMap().get(bidder).getBundle());
		}

		return this.createConcreteAuctionRoundBuilder(auction, restrictedBids, bidderMarginalsTemp);
	}

	protected abstract AuctionRoundBuilder<T> createConcreteAuctionRoundBuilder(Auction<T> auction,
			Map<Bidder, Set<Bundle>> restrictedBids, Map<UUID, List<ElicitationEconomy>> bidderMarginalsTemp);

	@Override
	public boolean phaseFinished(Auction<T> auction) {
		return auction.getMaximumSubmittedBids() >= this.getMaxQueries();
	}
}
