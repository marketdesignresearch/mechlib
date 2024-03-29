package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({ @PersistenceConstructor }))
public class ExactMLQueryAuctionRoundBuilder extends AuctionRoundBuilder<BundleExactValueBids> {

	@Getter
	private final Map<UUID, ExactValueQuery> interactions;

	private final Map<UUID, List<ElicitationEconomy>> marginalsToQueryNext;

	public ExactMLQueryAuctionRoundBuilder(Auction<BundleExactValueBids> auction,
			Map<UUID, ExactValueQuery> interactions, Map<UUID, List<ElicitationEconomy>> marginalsToQueryNext) {
		super(auction);
		this.interactions = interactions;
		this.marginalsToQueryNext = marginalsToQueryNext;
	}

	@Override
	public AuctionRound<BundleExactValueBids> build() {
		return new MLQueryAuctionRound<>(this.getAuction(),
				new BundleExactValueBids(interactions.entrySet().stream()
						.collect(Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()),
								e -> e.getValue().getBid(), (e1, e2) -> e1, LinkedHashMap::new))),
				marginalsToQueryNext);
	}

	@Override
	public BundleExactValueBids getTemporaryBids() {
		return new BundleExactValueBids(
				interactions.entrySet().stream().collect(Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()),
						e -> e.getValue().getBid(), (e1, e2) -> e1, LinkedHashMap::new)));
	}
}
