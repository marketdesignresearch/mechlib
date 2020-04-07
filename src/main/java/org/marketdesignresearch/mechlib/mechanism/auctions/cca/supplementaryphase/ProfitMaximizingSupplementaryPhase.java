package org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultPricedAuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultProfitMaxInteraction;

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
	public AuctionRoundBuilder<BundleExactValueBids> createNextRoundBuilder(Auction<BundleExactValueBids> auction) {
		Preconditions.checkState(auction.getLastRound() instanceof DefaultPricedAuctionRound);

		DefaultPricedAuctionRound<BundleExactValueBids> pricedRound = (DefaultPricedAuctionRound<BundleExactValueBids>) auction.getLastRound();
		return new ProfitMaximizingSupplementaryRoundBuilder(
				auction.getDomain().getBidders().stream()
						.collect(
								Collectors
										.toMap(Bidder::getId,
												b -> new DefaultProfitMaxInteraction(pricedRound.getPrices(),
														this.getNumberOfSupplementaryBids(), b.getId(), auction), (e1,e2)->e1, LinkedHashMap::new)),
				auction);
	}

	@Override
	public boolean phaseFinished(Auction<BundleExactValueBids> auction) {
		return auction.getCurrentPhaseRoundNumber() == 1;
	}

	@Override
	public String getType() {
		return "SUPPLEMENTARY PROFITMAX";
	}
}
