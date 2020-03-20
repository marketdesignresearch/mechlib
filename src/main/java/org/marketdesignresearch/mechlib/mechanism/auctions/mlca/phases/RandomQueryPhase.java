package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.springframework.data.annotation.PersistenceConstructor;

public abstract class RandomQueryPhase<T extends BundleValueBids<?>> implements AuctionPhase<T> {

	private static final int DEFAULT_NUMBER_OF_INITIAL_QUERIES = 30;
	
	private long seed;
	private final int numberOfInitialQueries;
	
	public RandomQueryPhase(long seed) {
		this(seed,DEFAULT_NUMBER_OF_INITIAL_QUERIES);
	}
	
	@PersistenceConstructor
	public RandomQueryPhase(long seed, int numberOfQueries) {
		this.seed = seed;
		this.numberOfInitialQueries = numberOfQueries;
	}
	
	@Override
	public AuctionRoundBuilder<T> createNextRoundBuilder(Auction<T> auction) {
		Set<Bundle> restrictedBids = new LinkedHashSet<>();

		int seedModifier = 0;

		while (restrictedBids.size() < this.numberOfInitialQueries) {
			// TODO remove old hacky code and use only one (potentially global) random object instead
			Random random = new Random(this.seed);
			for (int j = 0; j < seedModifier * auction.getDomain().getGoods().size(); j++) random.nextDouble();
			// end remove
			Bundle bundle = auction.getDomain().getRandomBundle(random);
			restrictedBids.add(bundle);
			seedModifier++;
		}
		
		Map<Bidder,Set<Bundle>> bidderRestrictedBids = new HashMap<>();
		auction.getDomain().getBidders().forEach(b -> bidderRestrictedBids.put(b, restrictedBids));

		return this.createConcreteAuctionRoundBuilder(auction, bidderRestrictedBids);
	}
	
	protected abstract AuctionRoundBuilder<T> createConcreteAuctionRoundBuilder(Auction<T> auction, Map<Bidder, Set<Bundle>> restrictedBids);

	@Override
	public boolean phaseFinished(Auction<T> auction) {
		return auction.getCurrentPhaseRoundNumber() == 1;
	}

	@Override
	public String getType() {
		return "RANDOM QUERY GENERATOR";
	}

}
