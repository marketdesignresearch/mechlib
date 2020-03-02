package org.marketdesignresearch.mechlib.mechanism.auctions.simple;

import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class SimpleBidPhase implements AuctionPhase<BundleValuePair> {

	@Override
	public AuctionRoundBuilder<BundleValuePair> createNextRoundBuilder(Auction<BundleValuePair> auction) {
		return new SimpleBidAuctionRoundBuilder(auction, 
				auction.getDomain().getBidders().stream().collect(
						Collectors.toMap(b -> b.getId(), b -> new DefaultSimpleBidInteraction(b.getId(), auction))));
	}

	@Override
	public boolean phaseFinished(Auction<BundleValuePair> auction) {
		return false;
	}

	@Override
	public String getType() {
		return "SIMPLE BID";
	}

}
