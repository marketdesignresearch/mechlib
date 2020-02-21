package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;

/**
 * Also check activity rules here for submitted bids
 * 
 * @author Manuel Beyeler
 *
 * @param <T>
 */
public interface Interaction {
	Bidder getBidder();
	
	Class<? extends Interaction> getType();
}
