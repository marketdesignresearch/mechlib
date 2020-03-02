package org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.Interaction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@PersistenceConstructor}))
public class LastBidsTrueValueSupplementaryRoundBuilder extends AuctionRoundBuilder<BundleValuePair> {

	private final Map<UUID, ExactValueQuery> interactions;

	public LastBidsTrueValueSupplementaryRoundBuilder(Map<UUID, ExactValueQuery> interactions, Auction<BundleValuePair> auction) {
		super(auction);
		this.interactions = interactions;
	}
	
	@Override
	public Map<UUID, ? extends Interaction<BundleValuePair>> getInteractions() {
		return interactions;
	}

	@Override
	public AuctionRound<BundleValuePair> build() {
		return new LastBidsTrueValueSupplementaryRound(this.getAuction(),this.collectBids());
	}
	
	private BundleValueBids<BundleValuePair> collectBids() {
		return new BundleValueBids<BundleValuePair>(interactions.entrySet().stream().filter(e -> e.getValue().getBid() != null)
				.collect(Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()), e -> e.getValue().getBid())));
	}

	@Override
	protected Outcome computeTemporaryResult(OutcomeRuleGenerator outcomeRuleGenerator) {
		return outcomeRuleGenerator.getOutcomeRule(this.collectBids()).getOutcome();
	}

}
