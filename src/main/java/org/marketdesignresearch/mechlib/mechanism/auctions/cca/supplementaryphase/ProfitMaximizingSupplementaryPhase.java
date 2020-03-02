package org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase;

import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultPricedAuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.CCAClockRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions.CCAProfitMaxInteraction;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ProfitMaximizingSupplementaryPhase implements SupplementaryPhase {

	private static final int DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS = 10;

	@Setter
	@Getter
	private int numberOfSupplementaryBids = DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS;

	public ProfitMaximizingSupplementaryPhase withNumberOfSupplementaryBids(int numberOfSupplementaryBids) {
		setNumberOfSupplementaryBids(numberOfSupplementaryBids);
		return this;
	}

	@Override
	public String getDescription() {
		return "Profit Maximizing Supplementary round with " + numberOfSupplementaryBids + " bids per bidder";
	}

	@Override
	public AuctionRoundBuilder<BundleValuePair> createNextRoundBuilder(Auction<BundleValuePair> auction) {
		Preconditions.checkState(auction.getLastRound() instanceof DefaultPricedAuctionRound);

		DefaultPricedAuctionRound<BundleValuePair> pricedRound = (DefaultPricedAuctionRound<BundleValuePair>) auction.getLastRound();
		return new ProfitMaximizingSupplementaryRoundBuilder(
				auction.getDomain().getBidders().stream()
						.collect(
								Collectors
										.toMap(b -> b.getId(),
												b -> new CCAProfitMaxInteraction(pricedRound.getPrices(),
														this.getNumberOfSupplementaryBids(), b.getId(), auction))),
				auction);
	}

	@Override
	public boolean phaseFinished(Auction<BundleValuePair> auction) {
		return auction.getCurrentPhaseRoundNumber() == 2;
	}

	@Override
	public String getType() {
		return "SUPPLEMENTARY PROFITMAX";
	}

}
