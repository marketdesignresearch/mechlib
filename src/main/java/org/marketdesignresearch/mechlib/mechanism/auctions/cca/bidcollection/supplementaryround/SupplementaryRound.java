package org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection.supplementaryround;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.CCAuction;

public interface SupplementaryRound {
    BundleValueBid getSupplementaryBids(CCAuction auction, Bidder bidder);
    int getNumberOfSupplementaryBids();

    default String getDescription() {
        return "(no description provided)";
    }
}
