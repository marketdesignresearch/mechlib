package org.marketdesignresearch.mechlib.mechanism.auctions.simple;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.Interaction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.SimpleBidInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.TypedInteraction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({ @PersistenceConstructor }))
public class SimpleBidAuctionRoundBuilder extends AuctionRoundBuilder<BundleExactValueBids>{

	private final Map<UUID, SimpleBidInteraction> interactions; 
	
	public SimpleBidAuctionRoundBuilder(Auction<BundleExactValueBids> auction, Map<UUID, SimpleBidInteraction> interactions) {
		super(auction);
		this.interactions = interactions;
	}
	
	@Override
	public Map<UUID, ? extends Interaction> getInteractions() {
		return interactions;
	}

	@Override
	public AuctionRound<BundleExactValueBids> build() {
		return new SimpleBidRound(this.getAuction(), this.collectBids());
	}

	@Override
	protected Outcome computeTemporaryResult(OutcomeRuleGenerator outcomeRuleGenerator) {
		return outcomeRuleGenerator.getOutcomeRule(this.collectBids()).getOutcome();
	}
	
	private BundleExactValueBids collectBids() {
		return new BundleExactValueBids(interactions.values().stream().filter(e -> e.getBid() != null).collect(Collectors.toMap(Interaction::getBidder, TypedInteraction::getBid, (e1, e2)->e1, LinkedHashMap::new)));
	}
	
}
