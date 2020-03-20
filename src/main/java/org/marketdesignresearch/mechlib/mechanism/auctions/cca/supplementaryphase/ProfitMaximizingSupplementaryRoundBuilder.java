package org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ProfitMaxQuery;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@PersistenceConstructor}))
public class ProfitMaximizingSupplementaryRoundBuilder extends AuctionRoundBuilder<BundleExactValueBids> {

	@Getter
	private final Map<UUID, ProfitMaxQuery> interactions;

	public ProfitMaximizingSupplementaryRoundBuilder(Map<UUID, ProfitMaxQuery> interactions, Auction<BundleExactValueBids> auction) {
		super(auction);
		this.interactions = interactions;
	}

	@Override
	public AuctionRound<BundleExactValueBids> build() {
		return new ProfitMaximizingSupplementaryRound(this.getAuction(),this.collectBids(),this.interactions.values().iterator().next().getPrices());
				
	}
	
	private BundleExactValueBids collectBids() {
		return new BundleExactValueBids(interactions.entrySet().stream().filter(e -> e.getValue().getBid() != null)
				.collect(Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()), e -> e.getValue().getBid())));
	}

	@Override
	protected Outcome computeTemporaryResult(OutcomeRuleGenerator outcomeRuleGenerator) {
		return outcomeRuleGenerator.getOutcomeRule(this.collectBids()).getOutcome();
	}

}
