package ch.uzh.ifi.ce.mechanisms.cca.supplementaryround;

import ch.uzh.ifi.ce.domain.*;
import lombok.Setter;

import java.util.Iterator;

public class LastBidsTrueValueSupplementaryRound implements SupplementaryRound {

    private static final int DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS = 500;

    @Setter
    private int numberOfSupplementaryBids = DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS;
    private Bids bids;

    public LastBidsTrueValueSupplementaryRound(Bids bids) {
        this.bids = bids;
    }

    @Override
    public Bid getSupplementaryBids(Bidder bidder) {
        Bid bid = bids.getBid(bidder);
        if (bid == null) return new Bid();
        Bid result = new Bid();
        int count = 0;
        // FIXME: Must be sorted bids -> Change CCA to include rounds
        Iterator<BundleBid> iterator = bid.getBundleBids().iterator();
        while (iterator.hasNext() && ++count < numberOfSupplementaryBids) {
            BundleBid bundleBid = iterator.next();
            BundleBid trueValuedBundleBid = new BundleBid(bidder.getValue(bundleBid.getBundle()), bundleBid.getBundle(), "TrueValued_" + bundleBid.getId());
            result.addBundleBid(trueValuedBundleBid);
        }
        return result;
    }

    public LastBidsTrueValueSupplementaryRound withNumberOfSupplementaryBids(int numberOfSupplementaryBids) {
        setNumberOfSupplementaryBids(numberOfSupplementaryBids);
        return this;
    }
}
