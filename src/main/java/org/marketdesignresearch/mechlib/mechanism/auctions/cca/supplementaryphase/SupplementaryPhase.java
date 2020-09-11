package org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;

/**
 * The supplementary phase of the combinatorial clock auction. Normally
 * a supplementary phase has only 1 round.
 * 
 * @author Manuel Beyeler
 */
public interface SupplementaryPhase extends AuctionPhase<BundleExactValueBids> {

	default String getDescription() {
		return "(no description provided)";
	}
}
