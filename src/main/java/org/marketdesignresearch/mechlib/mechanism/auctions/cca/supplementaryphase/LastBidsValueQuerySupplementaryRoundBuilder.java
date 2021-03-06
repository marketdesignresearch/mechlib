package org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({ @PersistenceConstructor }))
public class LastBidsValueQuerySupplementaryRoundBuilder extends AuctionRoundBuilder<BundleExactValueBids> {

	@Getter
	private final Map<UUID, ExactValueQuery> interactions;

	public LastBidsValueQuerySupplementaryRoundBuilder(Map<UUID, ExactValueQuery> interactions,
			Auction<BundleExactValueBids> auction) {
		super(auction);
		this.interactions = interactions;
	}

	@Override
	public AuctionRound<BundleExactValueBids> build() {
		return new LastBidsValueQuerySupplementaryRound(this.getAuction(), this.collectBids());
	}

	private BundleExactValueBids collectBids() {
		return new BundleExactValueBids(interactions.entrySet().stream().filter(e -> e.getValue().getBid() != null)
				.collect(Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()), e -> e.getValue().getBid(),
						(e1, e2) -> e1, LinkedHashMap::new)));
	}

	@Override
	public BundleExactValueBids getTemporaryBids() {
		return this.collectBids();
	}
}
