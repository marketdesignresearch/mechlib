package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultBoundValueQueryWithMRPARRefinementInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.MachineLearningComponent;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RefinementHelper.EfficiencyInfo;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices.LinearPriceGenerator;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;
import org.slf4j.Logger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BoundMLQueryWithMRPARPhase extends MLQueryPhase<BundleBoundValueBids> implements BidderRefinementRoundInfoCreator {

	private static final boolean DEFAULT_REFINE_MARGINAL_ECONOMIES = false;
	private static final boolean DEFAULT_INTERMEDIATE_REFINEMENTS = false;
	
	private final boolean refineMarginalEconomies;
	private final boolean intermediateRefinements;
	
	private List<ElicitationEconomy> refinementEconomies;
	
	public BoundMLQueryWithMRPARPhase(MachineLearningComponent<BundleBoundValueBids> mlComponent, long seed) {
		this(mlComponent, seed, DEFAULT_REFINE_MARGINAL_ECONOMIES, DEFAULT_INTERMEDIATE_REFINEMENTS);
	}
	
	public BoundMLQueryWithMRPARPhase(MachineLearningComponent<BundleBoundValueBids> mlComponent, long seed, boolean refineMarginalEconomies) {
		this(mlComponent,seed,refineMarginalEconomies, DEFAULT_INTERMEDIATE_REFINEMENTS);
	}
	
	public BoundMLQueryWithMRPARPhase(MachineLearningComponent<BundleBoundValueBids> mlComponent, long seed, boolean refineMarginalEconomies, boolean intermediateRefinments) {
		super(mlComponent, seed);
		this.refineMarginalEconomies = refineMarginalEconomies;
		this.intermediateRefinements = intermediateRefinments;
	}
	
	@Override
	public AuctionRoundBuilder<BundleBoundValueBids> createNextRoundBuilder(Auction<BundleBoundValueBids> auction) {
		if(this.refinementEconomies == null) {
			this.refinementEconomies = this.createAllRefinementEconomies(auction);
		}
		
		// check if requested and perform intermediate refinement
		if(intermediateRefinements && BoundMLQueryWithMRPARAuctionRound.class.isAssignableFrom(auction.getLastRound().getClass())) {
			return new RefinementAuctionRoundBuilder(auction, this.getSeed()+1, refinementEconomies, this.createEfficiencyInfo(auction));
		}
		
		return super.createNextRoundBuilder(auction);
	}

	@Override
	protected AuctionRoundBuilder<BundleBoundValueBids> createConcreteAuctionRoundBuilder(
			Auction<BundleBoundValueBids> auction, Map<Bidder, Set<Bundle>> restrictedBids,
			Map<UUID, List<ElicitationEconomy>> bidderMarginalsTemp, long nextRandomSeed) {
		
		Random random = new Random(nextRandomSeed);
		Map<UUID, BidderRefinementRoundInfo> bidderRefinementInfos = this.createBidderRefinementRoundInfos(auction, random, this.createEfficiencyInfo(auction));
		nextRandomSeed = random.nextLong();

		BundleBoundValueBids latestAggregatedBids = auction.getLatestAggregatedBids();
		
		return new BoundMLQueryWithMRPARAuctionRoundBuilder(auction,
				auction.getDomain().getBidders().stream()
						.collect(Collectors.toMap(Bidder::getId,
								b -> new DefaultBoundValueQueryWithMRPARRefinementInteraction(b.getId(), auction,
										bidderRefinementInfos.get(b.getId()).getAlphaAllocation().allocationOf(b).getBundle(), bidderRefinementInfos.get(b.getId()).getPrices(),
										latestAggregatedBids.getBid(b), restrictedBids.get(b)),
								(e1, e2) -> e1, LinkedHashMap::new)),
				bidderMarginalsTemp, nextRandomSeed, bidderRefinementInfos);
	}
	
	private Map<ElicitationEconomy, EfficiencyInfo> createEfficiencyInfo(Auction<BundleBoundValueBids> auction) {
		Map<ElicitationEconomy, EfficiencyInfo> efficiencyInfos = new LinkedHashMap<>();
		for(ElicitationEconomy economy : this.getRefinementEconomies()) {
			efficiencyInfos.put(economy, RefinementHelper.getRefinementInfo(auction, economy));
		}
		return efficiencyInfos;
	}
	
	protected List<ElicitationEconomy> createAllRefinementEconomies(Auction<BundleBoundValueBids> auction) {
		List<ElicitationEconomy> elicitationEconomies = new ArrayList<>();
		elicitationEconomies.add(new ElicitationEconomy(auction.getDomain()));
		if(this.refineMarginalEconomies)
			auction.getDomain().getBidders().forEach(bidder -> elicitationEconomies.add(new ElicitationEconomy(auction.getDomain(),bidder)));
		return elicitationEconomies;
	}

	
	@Override
	public List<ElicitationEconomy> getRefinementEconomies() {
		return refinementEconomies;
	}

	@Override
	public Logger getLogger() {
		return log;
	}

	@Override
	public Set<RefinementType> createRefinementType(BundleBoundValueBids bids) {
		return RefinementHelper.getMRPAR();
	}
}
