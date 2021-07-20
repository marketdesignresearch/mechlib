package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.ArrayList;
import java.util.List;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.BidderRefinementRoundInfoCreator;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.EfficiencyGuaranteeEfficiencyInfoCreator;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.EfficiencyInfo;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.EfficiencyInfoCreator;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.MRPAR_DIAR_RefinementRoundInfoCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * An alternate final phase for iMLCA that uses the refinement process described by Lubin et. al. (2008).
 * The process is based on the MRPAR and the DIAR activity rule.
 * 
 * @author Manuel Beyeler
 */
@RequiredArgsConstructor
public class RefinementPhase implements AuctionPhase<BundleBoundValueBids> {

	private transient int refinementInfoRound = -1;
	private transient EfficiencyInfo info;

	/**
	 * If set to true the refinement will also target the marginal economies (i.e. achieve the same efficiency guarantee for all marginal 
	 * economies as for the main economies.
	 */
	private final boolean refineMarginalEconomies;

	@Getter
	@Setter
	private BidderRefinementRoundInfoCreator refinementInfoCreator = new MRPAR_DIAR_RefinementRoundInfoCreator();

	@Getter
	@Setter
	private EfficiencyInfoCreator efficiencyInfoCreator = new EfficiencyGuaranteeEfficiencyInfoCreator();

	private List<ElicitationEconomy> allRefinementEconomies;

	/**
	 * @param refineMarginalEconomies if set to true the finement will also target the marginal economies
	 * @param timeLimit the timelimit for a CPLEX problem whil generating linear prices
	 */
	public RefinementPhase(boolean refineMarginalEconomies, double timeLimit) {
		this(refineMarginalEconomies);
		this.refinementInfoCreator.getPriceGenerator().setTimeLimit(timeLimit);
	}

	@Override
	public AuctionRoundBuilder<BundleBoundValueBids> createNextRoundBuilder(Auction<BundleBoundValueBids> auction) {
		this.updateInfo(auction);
		return new RefinementAuctionRoundBuilder(auction, this.getAllRefinementEconomies(auction), this.info,
				this.getRefinementInfoCreator());
	}

	@Override
	public boolean phaseFinished(Auction<BundleBoundValueBids> auction) {
		this.updateInfo(auction);
		return info.isConverged();
	}

	private void updateInfo(Auction<BundleBoundValueBids> auction) {
		if (this.refinementInfoRound != auction.getMaxRounds()) {
			this.info = this.getEfficiencyInfoCreator().getEfficiencyInfo(auction.getLatestAggregatedBids(),
					this.getAllRefinementEconomies(auction));
		}
	}

	@Override
	public String getType() {
		return "Refinement Phase";
	}

	protected List<ElicitationEconomy> createAllRefinementEconomies(Auction<BundleBoundValueBids> auction) {
		List<ElicitationEconomy> elicitationEconomies = new ArrayList<>();
		elicitationEconomies.add(new ElicitationEconomy(auction.getDomain()));
		if (this.refineMarginalEconomies)
			auction.getDomain().getBidders()
					.forEach(bidder -> elicitationEconomies.add(new ElicitationEconomy(auction.getDomain(), bidder)));
		return elicitationEconomies;
	}

	private List<ElicitationEconomy> getAllRefinementEconomies(Auction<BundleBoundValueBids> auction) {
		if (this.allRefinementEconomies == null) {
			this.allRefinementEconomies = this.createAllRefinementEconomies(auction);
		}
		return this.allRefinementEconomies;
	}
}
