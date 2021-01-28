package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ConvergenceInteraction;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({ @PersistenceConstructor }))
public class ConvergenceAuctionRoundBuilder extends AuctionRoundBuilder<BundleBoundValueBids> {

	@Getter
	private final Map<UUID, ConvergenceInteraction> interactions;

	public ConvergenceAuctionRoundBuilder(Auction<BundleBoundValueBids> auction,
			Map<UUID, ConvergenceInteraction> interactions) {
		super(auction);
		this.interactions = interactions;
	}

	@Override
	public AuctionRound<BundleBoundValueBids> build() {

		Map<Bidder, BundleBoundValueBid> bidMap = new LinkedHashMap<>();

		BigDecimal epsilon = BigDecimal.ZERO;

		// Validate submissions
		for (Map.Entry<UUID, ConvergenceInteraction> interaction : this.interactions.entrySet()) {
			BundleBoundValueBid bid = interaction.getValue().getBid();
			epsilon = interaction.getValue().getEpsilon();

			for (Bundle bundle : interaction.getValue().getBundles()) {
				BundleBoundValuePair pair = bid.getBidForBundle(bundle);

				BigDecimal interval = pair.getUpperBound().subtract(pair.getLowerBound());
				if (pair.getUpperBound().compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal uncertainty = interval.divide(pair.getUpperBound(), RoundingMode.HALF_UP);
					if (uncertainty.compareTo(epsilon) > 0) {
						throw new IllegalStateException("Bidder reported uncertainty of " + uncertainty
								+ "but was requested to report at most " + epsilon);
					}
				}
			}

			bidMap.put(this.getAuction().getBidder(interaction.getKey()), bid);
		}

		BundleBoundValueBids bids = new BundleBoundValueBids(bidMap);

		return new ConvergenceAuctionRound(getAuction(), bids, epsilon);
	}

	@Override
	public BundleBoundValueBids getTemporaryBids() {
		return new BundleBoundValueBids(
				interactions.entrySet().stream().collect(Collectors.toMap(e -> this.getAuction().getBidder(e.getKey()),
						e -> e.getValue().getBid(), (e1, e2) -> e1, LinkedHashMap::new)));
	}

}
