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
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;
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

/**
 * The machine learning phase of MLCA. See Brero et. al. (2020) for details.
 * 
 * This phase respects {@link AllocationLimit}s. This means only ValueQueries for allocatable bundles 
 * are issued.
 * 
 * @author Manuel Beyeler
 *
 * @param <T> the bid type of this auction
 */
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

		Map<Bidder, Set<Bundle>> queries = new LinkedHashMap<>();
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

		Map<ElicitationEconomy, Allocation> infMarginalEconomies = new LinkedHashMap<>();
		// Marginal Elicitation
		for (Bidder bidder : auction.getDomain().getBidders()) {
			log.info("Add marginal queries for Bidder {}", bidder.getName());

			bidderMarginalsTemp.putIfAbsent(bidder.getId(), new ArrayList<>());
			queries.putIfAbsent(bidder, new LinkedHashSet<>());

			for (int i = 0; i < marginalQueries; i++) {
				// If all marginals were processed refill marginal list
				if (bidderMarginalsTemp.get(bidder.getId()).isEmpty())
					bidderMarginalsTemp.put(bidder.getId(), this.marginalEconomies.stream()
							.filter(me -> me.getBidders().contains(bidder.getId())).collect(Collectors.toList()));

				// choose random setting and remove from setting list
				ElicitationEconomy economy = bidderMarginalsTemp.get(bidder.getId()).remove(
						auction.getCurrentRoundRandom().nextInt(bidderMarginalsTemp.get(bidder.getId()).size()));

				Bundle query = infMarginalEconomies.computeIfAbsent(economy, e -> mlai.getInferredEfficientAllocation(auction.getDomain(), e)).allocationOf(bidder).getBundle();
				if(queries.get(bidder).contains(query) || auction.getLatestAggregatedBids().getBid(bidder).getBidForBundle(query) != null) {
					System.out.println("new WDP Marginal");
			
					Allocation inferredEfficientAllocation = mlai
						.getInferredEfficientAllocation(auction.getDomain(), economy,
								Map.of(bidder, Stream.concat(
												auction.getLatestAggregatedBids().getBid(bidder).getBundleBids()
														.stream().map(bb -> bb.getBundle()),
												queries.get(bidder).stream())
										.collect(Collectors.toCollection(LinkedHashSet::new))));
					query = inferredEfficientAllocation.allocationOf(bidder).getBundle();
				}
				log.info(economy.toString() + " New bundle: "
						+ query);
				queries.get(bidder).add(query);
			}
		}

		// Main Elicitation
		Allocation infAllocationMain = mlai.getInferredEfficientAllocation(auction.getDomain(), this.mainEconomy);
		for (Bidder bidder : auction.getDomain().getBidders()) {
			
			Bundle query = infAllocationMain.allocationOf(bidder).getBundle();
			
			if(queries.get(bidder).contains(query) || auction.getLatestAggregatedBids().getBid(bidder).getBidForBundle(query) != null) {
				System.out.println("new WDP main");
				Allocation infAllocation = mlai
						.getInferredEfficientAllocation(
								auction.getDomain(), this.mainEconomy, Map
								.of(bidder, Stream
										.concat(Stream.concat(
												auction.getLatestAggregatedBids().getBid(bidder).getBundleBids()
												.stream().map(bb -> bb.getBundle()),
													queries.get(bidder).stream()), Stream.of(Bundle.EMPTY))
											.collect(Collectors.toCollection(LinkedHashSet::new))));
				query = infAllocation.allocationOf(bidder).getBundle();
			}
			log.info("Bidder {}: Main Economy New bundle: {}", bidder.getName(), query);
			queries.get(bidder).add(query);
		}

		return this.createConcreteAuctionRoundBuilder(auction, queries, bidderMarginalsTemp);
	}

	protected abstract AuctionRoundBuilder<T> createConcreteAuctionRoundBuilder(Auction<T> auction,
			Map<Bidder, Set<Bundle>> restrictedBids, Map<UUID, List<ElicitationEconomy>> bidderMarginalsTemp);

	@Override
	public boolean phaseFinished(Auction<T> auction) {
		return auction.getMaximumSubmittedBids() >= this.getMaxQueries();
	}
}
