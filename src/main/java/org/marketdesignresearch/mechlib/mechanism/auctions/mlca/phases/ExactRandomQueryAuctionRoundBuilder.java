package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({ @PersistenceConstructor }))
public class ExactRandomQueryAuctionRoundBuilder extends AuctionRoundBuilder<BundleExactValueBids> {

	@Getter
	private final Map<UUID, ExactValueQuery> interactions;
	
	public ExactRandomQueryAuctionRoundBuilder(Auction<BundleExactValueBids> auction, Map<UUID, ExactValueQuery> interactions) {
		super(auction);
		this.interactions = interactions;
	}

	@Override
	public AuctionRound<BundleExactValueBids> build() {
		return new RandomQueryAuctionRound<>(this.getAuction(),
				new BundleExactValueBids(this.interactions.entrySet().stream().collect(
						Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()), e -> e.getValue().getBid()))));
	}

	@Override
	protected Outcome computeTemporaryResult(OutcomeRuleGenerator outcomeRuleGenerator) {
		// TODO Auto-generated method stub
		return null;
	}
}
