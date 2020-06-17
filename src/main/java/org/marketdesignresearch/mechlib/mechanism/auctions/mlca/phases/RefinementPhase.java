package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RefinementHelper.EfficiencyInfo;
import org.slf4j.Logger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RefinementPhase implements AuctionPhase<BundleBoundValueBids>, BidderRefinementRoundInfoCreator {

	private static final BigDecimal DEFAULT_EFFICIENCY_TOLERANCE = BigDecimal.valueOf(0.99);
	private static final int DEFAULT_MAX_NUMBER_OF_ROUNDS = 30;
	
	private transient int refinementInfoRound = -1;
	private transient Map<ElicitationEconomy,EfficiencyInfo> info = new LinkedHashMap<>();

	private final BigDecimal efficientyTolerance;
	private final int maxNumberOfRounds;
	private final long seed;
	private final boolean refineMarginalEconomies;
	
	private List<ElicitationEconomy> allRefinementEconomies;

	public RefinementPhase(long seed, boolean refineMarginalEconomies) {
		this(DEFAULT_EFFICIENCY_TOLERANCE, DEFAULT_MAX_NUMBER_OF_ROUNDS, seed, refineMarginalEconomies);
	}

	@Override
	public AuctionRoundBuilder<BundleBoundValueBids> createNextRoundBuilder(Auction<BundleBoundValueBids> auction) {
		if(this.allRefinementEconomies == null) {
			this.allRefinementEconomies = this.createAllRefinementEconomies(auction);
		}
		
		this.updateInfo(auction);

		Random random = new Random(seed);
		for (int i = auction.getNumberOfRounds() - 1; i >= 0; i--) {
			AuctionRound<BundleBoundValueBids> auctionRound = auction.getRound(i);
			// Find last RefinementAuctionRound
			if (DefaultRefinementAuctionRound.class.isAssignableFrom(auctionRound.getClass())) {
				random = new Random(((DefaultRefinementAuctionRound) auctionRound).getSeedNextRound());
				break;
			}
		}
		
		Map<UUID, BidderRefinementRoundInfo> bidderRefinementInfos = this.createBidderRefinementRoundInfos(auction, random, info);
		
		return new RefinementAuctionRoundBuilder(auction,bidderRefinementInfos, random.nextLong());
	}

	@Override
	public boolean phaseFinished(Auction<BundleBoundValueBids> auction) {
		this.updateInfo(auction);
		return info.values().stream().map(i -> i.efficiency.compareTo(this.efficientyTolerance) >= 0).reduce(false, Boolean::logicalOr)				
				|| auction.getCurrentPhaseRoundNumber() == this.maxNumberOfRounds;
	}

	private void updateInfo(Auction<BundleBoundValueBids> auction) {
		if (this.refinementInfoRound != auction.getMaxRounds()) {
			for(ElicitationEconomy economy : this.allRefinementEconomies) {
				info.put(economy, RefinementHelper.getRefinementInfo(auction, economy));
			}
		}
	}

	@Override
	public String getType() {
		return "Refinement Phase";
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
		return this.allRefinementEconomies;
	}
	
	public Logger getLogger() {
		return log;
	}

	@Override
	public Set<RefinementType> createRefinementType(BundleBoundValueBids bids) {
		return RefinementHelper.getMRPARAndDIAR(bids);
	}
}