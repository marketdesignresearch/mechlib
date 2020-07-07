package org.marketdesignresearch.mechlib.mechanism.auctions.simple;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class SimpleBidPhase implements AuctionPhase<BundleExactValueBids> {

	@Override
	public AuctionRoundBuilder<BundleExactValueBids> createNextRoundBuilder(Auction<BundleExactValueBids> auction) {
		return new SimpleBidAuctionRoundBuilder(auction,
				auction.getDomain().getBidders().stream().collect(Collectors.toMap(b -> b.getId(),
						b -> new DefaultSimpleBidInteraction(b.getId(), auction), (e1, e2) -> e1, LinkedHashMap::new)));
	}

	@Override
	public boolean phaseFinished(Auction<BundleExactValueBids> auction) {
		return false;
	}

	@Override
	public String getType() {
		return "SIMPLE BID";
	}

}
