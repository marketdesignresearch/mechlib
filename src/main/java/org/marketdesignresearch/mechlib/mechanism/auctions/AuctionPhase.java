package org.marketdesignresearch.mechlib.mechanism.auctions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;

/**
 * An AuctionPhase can consist of multiple rounds. The auction
 * creates an AuctionRoundBuilder for every round that must handle 
 * one round of this phase.
 * 
 * Note that the AuctionPhase itself should not contain any state itself.
 * The state of the AuctionPhase should be completely captured by the 
 * AuctionRounds generated by the AuctionRoundBuilder. This enables an 
 * easy rollback of any Auction instance to any round. ({@link Auction#resetToRound(int)}). 
 * 
 * @author Manuel Beyeler
 *
 * @param <BB> the type of bids the auction is based on
 */
public interface AuctionPhase<BB extends BundleValueBids<?>> {
	
	AuctionRoundBuilder<BB> createNextRoundBuilder(Auction<BB> auction);
	
	/**
	 * Method that can be invoked by the auction to check whether this phase 
	 * has more rounds.
	 * 
	 * Note that the return value of this method is only defined if this phase
	 * is the current active phase of an auction.
	 *  
	 * @param auction the auction object as context
	 * @return true if this phase has no more rounds, otherwise false
	 */
	boolean phaseFinished(Auction<BB> auction);
	
	String getType();
}
