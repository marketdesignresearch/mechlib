package org.marketdesignresearch.mechlib.mechanism.auctions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;

/**
 * An AuctionRound holds the state (i.e. the result) of an AuctionRound.
 * It may also hold the internal state of an AuctionPhase such that the
 * Auction can be rolled back to any AuctionRound and continue at 
 * exactly the same state before.
 * 
 * @author Manuel Beyeler
 *
 * @param <BB> the bid type of the respective Auction
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
