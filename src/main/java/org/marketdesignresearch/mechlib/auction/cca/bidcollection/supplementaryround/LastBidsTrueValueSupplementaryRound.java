package org.marketdesignresearch.mechlib.auction.cca.bidcollection.supplementaryround;

import lombok.Getter;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.BundleBid;
import org.marketdesignresearch.mechlib.auction.cca.CCAuction;
import lombok.Setter;

import java.util.Iterator;

public class LastBidsTrueValueSupplementaryRound implements SupplementaryRound {

    private static final int DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS = 500;

    @Setter @Getter
    private int numberOfSupplementaryBids = DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS;
    private final Bids bids;

    public LastBidsTrueValueSupplementaryRound(CCAuction auction) {
        this.bids = auction.getLatestAggregatedBids();
    }

    @Override
    public Bid getSupplementaryBids(String id, Bidder bidder) {
        Bid bid = bids.getBid(bidder);
        if (bid == null) return new Bid();
        Bid result = new Bid();
        int count = 0;
        // TODO: This is not ordered nor unique. If needed, consider storing BundleBids in a List and filtering duplicates
        Iterator<BundleBid> iterator = bid.getBundleBids().iterator();
        while (iterator.hasNext() && ++count < numberOfSupplementaryBids) {
            BundleBid bundleBid = iterator.next();
            BundleBid trueValuedBundleBid = new BundleBid(bidder.getValue(bundleBid.getBundle()), bundleBid.getBundle(), "TrueValued_" + id + "_" + bundleBid.getId());
            result.addBundleBid(trueValuedBundleBid);
        }
        return result;
    }

    public LastBidsTrueValueSupplementaryRound withNumberOfSupplementaryBids(int numberOfSupplementaryBids) {
        setNumberOfSupplementaryBids(numberOfSupplementaryBids);
        return this;
    }

    @Override
    public String getDescription() {
        return "Supplementary round to submit the true values of the last " + numberOfSupplementaryBids + " bids";
    }
}
