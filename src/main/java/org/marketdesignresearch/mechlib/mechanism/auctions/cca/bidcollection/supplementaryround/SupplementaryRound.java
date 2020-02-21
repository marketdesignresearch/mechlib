package org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection.supplementaryround;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.CCAuction;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions.BundleValueTransformableInteraction;

public interface SupplementaryRound {
	BundleValueTransformableInteraction<BundleValuePair> getInteraction(CCAuction auction, Bidder bidder);
    int getNumberOfSupplementaryBids();

    default String getDescription() {
        return "(no description provided)";
    }
}
