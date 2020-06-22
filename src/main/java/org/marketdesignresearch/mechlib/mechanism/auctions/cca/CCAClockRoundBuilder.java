package org.marketdesignresearch.mechlib.mechanism.auctions.cca;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.demand.DemandBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DemandQuery;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({ @PersistenceConstructor }))
public class CCAClockRoundBuilder extends AuctionRoundBuilder<BundleExactValueBids> {

	@Getter
	private final Map<UUID, DemandQuery> interactions;

	public CCAClockRoundBuilder(Map<UUID, DemandQuery> interactions, Auction<BundleExactValueBids> auction) {
		super(auction);
		this.interactions = interactions;
	}

	@Override
	public AuctionRound<BundleExactValueBids> build() {
		log.info("Building Clock Round {}", this.getAuction().getNumberOfRounds() + 1);
		return new CCAClockRound(this.getAuction(), this.collectBids() , interactions.values().iterator().next().getPrices(),
				this.getAuction().getDomain().getGoods());
	}
	
	private DemandBids collectBids() {
		return new DemandBids(this.interactions.entrySet().stream().filter(e -> e.getValue().getBid() != null)
				.collect(Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()), e -> e.getValue().getBid(), (e1,e2)->e1, LinkedHashMap::new)));
	}

	@Override
	protected Outcome computeTemporaryResult(OutcomeRuleGenerator outcomeRuleGenerator) {
		return outcomeRuleGenerator.getOutcomeRule(this.collectBids().transformToBundleValueBids(this.interactions.values().iterator().next().getPrices())).getOutcome();
	}
}
