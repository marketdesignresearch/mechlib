package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.BoundValueQueryWithMRPARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.validator.ICEValidator;

import com.google.common.base.Preconditions;

import lombok.Getter;

public class BoundMLQueryWithMRPARAuctionRoundBuilder extends AuctionRoundBuilder<BundleBoundValueBids> {
	@Getter
	private Map<UUID, BoundValueQueryWithMRPARRefinement> interactions;

	private Map<UUID, BundleBoundValueBid> original = new LinkedHashMap<>();

	private final Map<UUID, List<ElicitationEconomy>> marginalsToQueryNext;
	private final Map<UUID, BidderRefinementRoundInfo> refinementInfos;

	public BoundMLQueryWithMRPARAuctionRoundBuilder(Auction<BundleBoundValueBids> auction,
			Map<UUID, BoundValueQueryWithMRPARRefinement> interactions,
			Map<UUID, List<ElicitationEconomy>> marginalsToQueryNext,
			Map<UUID, BidderRefinementRoundInfo> refinementInfos) {
		super(auction);
		this.interactions = interactions;
		this.interactions.entrySet().forEach(e -> original.put(e.getKey(), e.getValue().getLatestActiveBid().copy()));
		this.marginalsToQueryNext = marginalsToQueryNext;
		this.refinementInfos = refinementInfos;
	}

	@Override
	public AuctionRound<BundleBoundValueBids> build() {
		// Validate submissions
		interactions.entrySet()
				.forEach(e -> ICEValidator.validateRefinement(original.get(e.getKey()), e.getValue().getBid(),
						e.getValue().getPrices(), e.getValue().getProvisionalAllocation(),
						RefinementHelper.getMRPAR()));

		// consistency of the bid was already validated before (i.e. if for every bundle
		// of the original bid a bid was submitted)
		for (Map.Entry<UUID, BoundValueQueryWithMRPARRefinement> entry : interactions.entrySet()) {
			// consistency of the bid was already validated before (i.e. if for every bundle
			// of the original bid a bid was submitted)
			Preconditions.checkState(entry.getValue().getBid().getBundleBids().size() == original.get(entry.getKey())
					.getBundleBids().size() + entry.getValue().getQueriedBundles().size());
			// check if values for all queried bundles were submitted
			Preconditions
					.checkState(entry.getValue().getBid().getBundleBids().stream().map(BundleBoundValuePair::getBundle)
							.collect(Collectors.toList()).containsAll(entry.getValue().getQueriedBundles()));
		}

		return new BoundMLQueryWithMRPARAuctionRound(this.getAuction(),
				new BundleBoundValueBids(interactions.entrySet().stream()
						.collect(Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()),
								e -> e.getValue().getBid(), (e1, e2) -> e1, LinkedHashMap::new))),
				marginalsToQueryNext, this.refinementInfos);
	}

	@Override
	public BundleBoundValueBids getTemporaryBids() {
		return new BundleBoundValueBids(
				interactions.entrySet().stream().collect(Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()),
						e -> e.getValue().getBid(), (e1, e2) -> e1, LinkedHashMap::new)));
	}
}
