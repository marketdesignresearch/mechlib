package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultRefinementQueryInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases.RefinementHelper.EfficiencyInfo;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.validator.ICEValidator;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.slf4j.Logger;

import com.google.common.base.Preconditions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefinementAuctionRoundBuilder extends AuctionRoundBuilder<BundleBoundValueBids> implements BidderRefinementRoundInfoCreator{

	@Getter
	private final Map<UUID, RefinementQuery> interactions;
	private final Map<UUID,BidderRefinementRoundInfo> refinementInfos;
	@Getter
	private final List<ElicitationEconomy> refinementEconomies;

	private final Map<UUID, BundleBoundValueBid> original = new LinkedHashMap<>();

	public RefinementAuctionRoundBuilder(Auction<BundleBoundValueBids> auction, List<ElicitationEconomy> refinementEconomies, Map<ElicitationEconomy, EfficiencyInfo> efficiencyInfos) {
		super(auction);
		this.refinementEconomies = refinementEconomies;
		
		this.refinementInfos = this.createBidderRefinementRoundInfos(auction, auction.getCurrentRoundRandom(), efficiencyInfos);
		
		BundleBoundValueBids latestAggregatedBids = auction.getLatestAggregatedBids();
		this.interactions = new LinkedHashMap<>();

		for (Bidder bidder : auction.getDomain().getBidders()) {
			BidderRefinementRoundInfo info = this.refinementInfos.get(bidder.getId());
			interactions.put(bidder.getId(),new DefaultRefinementQueryInteraction(bidder.getId(), auction, info.getRefinements(),
					info.getAlphaAllocation().allocationOf(bidder).getBundle(), info.getPrices(),
					latestAggregatedBids.getBid(bidder)));
			this.original.put(bidder.getId(), latestAggregatedBids.getBid(bidder).copy());
		}
	}

	@Override
	public AuctionRound<BundleBoundValueBids> build() {
		// Validate submissions
		interactions.entrySet()
				.forEach(e -> ICEValidator.validateRefinement(original.get(e.getKey()), e.getValue().getBid(),
						e.getValue().getPrices(), e.getValue().getProvisonalAllocation(),
						e.getValue().getRefinementTypes()));
		// check if no new bids were added
		// consistency of the bid was already validated before (i.e. if for every bundle of the original bid a bid was submitted)
		interactions.entrySet().forEach(e -> Preconditions.checkState(e.getValue().getBid().getBundleBids().size() == original.get(e.getKey()).getBundleBids().size()));
		
		return new DefaultRefinementAuctionRound(this.getAuction(), new BundleBoundValueBids(interactions.entrySet().stream().collect(
						Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()), e -> e.getValue().getBid()))),
				refinementInfos);
	}

	@Override
	protected Outcome computeTemporaryResult(OutcomeRuleGenerator outcomeRuleGenerator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<RefinementType> createRefinementType(BundleBoundValueBids bids) {
		return RefinementHelper.getMRPARAndDIAR(bids);
	}

	@Override
	public Logger getLogger() {
		return log;
	}

}
