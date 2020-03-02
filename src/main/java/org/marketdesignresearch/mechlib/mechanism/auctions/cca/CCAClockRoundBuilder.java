package org.marketdesignresearch.mechlib.mechanism.auctions.cca;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bid.demand.DemandBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DemandQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.Interaction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({ @PersistenceConstructor }))
public class CCAClockRoundBuilder extends AuctionRoundBuilder<BundleValuePair> {

	private final Map<UUID, DemandQuery> interactions;

	public CCAClockRoundBuilder(Map<UUID, DemandQuery> interactions, Auction<BundleValuePair> auction) {
		super(auction);
		this.interactions = interactions;
	}

	@Override
	public Map<UUID, ? extends Interaction<BundleValuePair>> getInteractions() {
		return this.interactions;
	}

	@Override
	public AuctionRound<BundleValuePair> build() {
		log.info("Building Clock Round {}", this.getAuction().getNumberOfRounds() + 1);
		return new CCAClockRound(this.getAuction(), this.collectBids() , interactions.values().iterator().next().getPrices(),
				this.getAuction().getDomain().getGoods());
	}
	
	private DemandBids collectBids() {
		return new DemandBids(this.interactions.entrySet().stream().filter(e -> e.getValue().getBid() != null)
				.collect(Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()), e -> e.getValue().getBid())));
	}

	@Override
	protected Outcome computeTemporaryResult(OutcomeRuleGenerator outcomeRuleGenerator) {
		return outcomeRuleGenerator.getOutcomeRule(this.collectBids().transformToBundleValueBids(this.interactions.values().iterator().next().getPrices())).getOutcome();
	}
}
