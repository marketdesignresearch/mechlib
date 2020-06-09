package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.math.BigDecimal;
import java.util.List;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RefinementHelper.RefinementInfo;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices.LinearPriceGenerator;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

public class RefinementPhase implements AuctionPhase<BundleBoundValueBids> {

	private transient int refinementInfoRound;
	private transient RefinementInfo info;

	private final BigDecimal efficientyTolerance;
	private final int maxNumberOfRounds;

	public RefinementPhase(BigDecimal efficiencyTolerance, int maxNumberOfRounds) {
		this.efficientyTolerance = efficiencyTolerance;
		this.maxNumberOfRounds = maxNumberOfRounds;
	}

	@Override
	public AuctionRoundBuilder<BundleBoundValueBids> createNextRoundBuilder(Auction<BundleBoundValueBids> auction) {
		this.updateInfo(auction);

		BundleExactValueBids alphaValuation = auction.getLatestAggregatedBids().getAlphaBids(info.alpha);
		Allocation alphaAllocation = new XORWinnerDetermination(alphaValuation).getAllocation();
		BundleExactValueBids perturbedValuation = auction.getLatestAggregatedBids().getPerturbedBids(alphaAllocation);

		Prices prices = LinearPriceGenerator.getPrices(auction.getDomain(), new ElicitationEconomy(auction.getDomain()),
				alphaAllocation, List.of(alphaValuation, perturbedValuation), true);
		
		return new RefinementAuctionRoundBuilder(auction, prices, alphaAllocation);
	}

	@Override
	public boolean phaseFinished(Auction<BundleBoundValueBids> auction) {
		this.updateInfo(auction);
		return info.efficiency.compareTo(this.efficientyTolerance) >= 0
				|| auction.getCurrentPhaseRoundNumber() == this.maxNumberOfRounds;
	}

	private void updateInfo(Auction<BundleBoundValueBids> auction) {
		if (info == null || this.refinementInfoRound != auction.getMaxRounds()) {
			info = RefinementHelper.getRefinementInfo(auction);
		}
	}

	@Override
	public String getType() {
		return "Refinement Phase";
	}
}
