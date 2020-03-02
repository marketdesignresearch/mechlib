package org.marketdesignresearch.mechlib.mechanism.auctions.simple;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.Interaction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.SimpleBidInteraction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({ @PersistenceConstructor }))
public class SimpleBidAuctionRoundBuilder extends AuctionRoundBuilder<BundleValuePair>{

	private final Map<UUID, SimpleBidInteraction> interactions; 
	
	public SimpleBidAuctionRoundBuilder(Auction<BundleValuePair> auction, Map<UUID, SimpleBidInteraction> interactions) {
		super(auction);
		this.interactions = interactions;
	}
	
	@Override
	public Map<UUID, ? extends Interaction<BundleValuePair>> getInteractions() {
		return interactions;
	}

	@Override
	public AuctionRound<BundleValuePair> build() {
		return new SimpleBidRound(this.getAuction(), this.collectBids());
	}

	@Override
	protected Outcome computeTemporaryResult(OutcomeRuleGenerator outcomeRuleGenerator) {
		return outcomeRuleGenerator.getOutcomeRule(this.collectBids()).getOutcome();
	}
	
	private BundleValueBids<BundleValuePair> collectBids() {
		return new BundleValueBids<>(interactions.values().stream().filter(e -> e.getBid() != null).collect(Collectors.toMap(e -> e.getBidder(), e-> e.getBid())));
	}
	
}
