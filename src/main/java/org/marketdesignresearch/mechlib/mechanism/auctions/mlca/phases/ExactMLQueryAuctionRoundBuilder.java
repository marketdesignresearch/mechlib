package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@PersistenceConstructor}))
public class ExactMLQueryAuctionRoundBuilder extends AuctionRoundBuilder<BundleExactValueBids>{

	@Getter
	private Map<UUID, ExactValueQuery> interactions;
	
	private final Map<UUID, List<ElicitationEconomy>> marginalsToQueryNext;
	private final long seedNextRound;
	
	
	public ExactMLQueryAuctionRoundBuilder(Auction<BundleExactValueBids> auction, Map<UUID, ExactValueQuery> interactions,Map<UUID, List<ElicitationEconomy>> marginalsToQueryNext,long seed) {
		super(auction);
		this.interactions = interactions;
		this.marginalsToQueryNext = marginalsToQueryNext;
		this.seedNextRound = seed;
	}

	@Override
	public AuctionRound<BundleExactValueBids> build() {
		// TODO check if all interactions completed
		return new MLQueryAuctionRound<>(this.getAuction(), new BundleExactValueBids(interactions.entrySet().stream().collect(Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()), e -> e.getValue().getBid()))),marginalsToQueryNext,seedNextRound);
	}

	@Override
	protected Outcome computeTemporaryResult(OutcomeRuleGenerator outcomeRuleGenerator) {
		// TODO Auto-generated method stub
		return null;
	}

}
