package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.allocationlimits.AllocationLimit;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.random.BidderRandom;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.springframework.data.annotation.PersistenceConstructor;

/**
 * Initial phase of MLCA. Each bidder receives {@link #numberOfInitialQueries}
 * random value queries.
 * 
 * This phase respects {@link AllocationLimit}s. This means only ValueQueries
 * for allocatable bundles are issued.
 * 
 * @author Manuel Beyeler
 *
 * @param <T> the bid type of this auction
 */
public abstract class RandomQueryPhase<T extends BundleValueBids<?>> implements AuctionPhase<T> {

	private static final int DEFAULT_NUMBER_OF_INITIAL_QUERIES = 30;

	private final int numberOfInitialQueries;

	public RandomQueryPhase() {
		this(DEFAULT_NUMBER_OF_INITIAL_QUERIES);
	}

	/**
	 * @param numberOfQueries the number of initial queries per bidder.
	 */
	@PersistenceConstructor
	public RandomQueryPhase(int numberOfQueries) {
		this.numberOfInitialQueries = numberOfQueries;
	}

	@Override
	public AuctionRoundBuilder<T> createNextRoundBuilder(Auction<T> auction) {
		Random random = BidderRandom.INSTANCE.getRandom();
		Map<Bidder, Set<Bundle>> bidderRestrictedBids = new LinkedHashMap<>();

		for (Bidder b : auction.getDomain().getBidders()) {
			int bidderMaxQueries = Math.min(this.numberOfInitialQueries,
					b.getAllocationLimit().calculateAllocationBundleSpace(auction.getDomain().getGoods()));

			bidderRestrictedBids.put(b, new LinkedHashSet<>());
			while (bidderRestrictedBids.get(b).size() < bidderMaxQueries) {
				Bundle bundle = b.getAllocationLimit().getUniformRandomBundle(random, auction.getDomain().getGoods());
				bidderRestrictedBids.get(b).add(bundle);
			}
		}

		return this.createConcreteAuctionRoundBuilder(auction, bidderRestrictedBids);
	}

	protected abstract AuctionRoundBuilder<T> createConcreteAuctionRoundBuilder(Auction<T> auction,
			Map<Bidder, Set<Bundle>> restrictedBids);

	@Override
	public boolean phaseFinished(Auction<T> auction) {
		return auction.getCurrentPhaseRoundNumber() == 1;
	}
}
