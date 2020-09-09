package org.marketdesignresearch.mechlib.mechanism.auctions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;

/**
 * 
 * TODO add documentation
 * 
 * @author Manuel Beyeler
 *
 * @param <BB>
 */
public interface AuctionRound<BB extends BundleValueBids<?>> {

	int getRoundNumber();

	int getAuctionPhaseNumber();

	int getAuctionPhaseRoundNumber();

	BB getBids();

	default String getDescription() {
		return "Auction round " + getRoundNumber();
	}
}
