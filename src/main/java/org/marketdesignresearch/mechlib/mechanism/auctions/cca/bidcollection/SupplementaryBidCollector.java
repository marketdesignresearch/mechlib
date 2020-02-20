package org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.CCAuction;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection.supplementaryround.SupplementaryRound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SupplementaryBidCollector {

    private final CCAuction auction;
    private final SupplementaryRound supplementaryRound; 

    public BundleValueBids collectBids() {
        BundleValueBids bids = new BundleValueBids();
        for (Bidder bidder : this.auction.getDomain().getBidders()) {
            log.debug("Asking bidder {} for additional bids...", bidder.getName());
            bids.setBid(bidder, supplementaryRound.getSupplementaryBids(auction, bidder));
            log.debug("Done, bids for bidder {} collected", bidder.getName());
        }
        return bids;
    }

    @Override
    public String toString() {
        return supplementaryRound.getDescription();
    }

}
