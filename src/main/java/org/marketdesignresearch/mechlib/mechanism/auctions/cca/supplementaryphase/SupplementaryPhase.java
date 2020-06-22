package org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;

public interface SupplementaryPhase extends AuctionPhase<BundleExactValueBids>{

    default String getDescription() {
        return "(no description provided)";
    }
}
