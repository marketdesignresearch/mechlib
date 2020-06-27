package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.BoundValueQuery;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({ @PersistenceConstructor }))
public class BoundRandomQueryAuctionRoundBuilder extends AuctionRoundBuilder<BundleBoundValueBids> {

	@Getter
	private final Map<UUID, BoundValueQuery> interactions;
	
	public BoundRandomQueryAuctionRoundBuilder(Auction<BundleBoundValueBids> auction, Map<UUID, BoundValueQuery> interactions) {
		super(auction);
		this.interactions = interactions;
	}

	@Override
	public AuctionRound<BundleBoundValueBids> build() {
		return new RandomQueryAuctionRound<>(this.getAuction(),
				new BundleBoundValueBids(this.interactions.entrySet().stream().collect(
						Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()), e -> e.getValue().getBid(), (e1, e2) -> e1, LinkedHashMap::new))));
	}

	@Override
	protected Outcome computeTemporaryResult(OutcomeRuleGenerator outcomeRuleGenerator) {
		// TODO Auto-generated method stub
		return null;
	}
}
