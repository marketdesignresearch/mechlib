package org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection.supplementaryround;

import java.util.Iterator;

import org.marketdesignresearch.mechlib.core.BundleBid;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.CCAuction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class LastBidsTrueValueSupplementaryRound implements SupplementaryRound {

    private static final int DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS = 500;

    @Setter @Getter
    private int numberOfSupplementaryBids = DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS;

    public LastBidsTrueValueSupplementaryRound() {
    }

    @Override
    public Bid getSupplementaryBids(CCAuction auction, Bidder bidder) {
        Bid bid = auction.getLatestAggregatedBids(bidder);
        if (bid == null) return new Bid();
        Bid result = new Bid();
        int count = 0;
        // TODO: This is not ordered nor unique. If needed, consider storing BundleBids in a List and filtering duplicates
        Iterator<BundleBid> iterator = bid.getBundleBids().iterator();
        while (iterator.hasNext() && ++count < numberOfSupplementaryBids) {
            BundleBid bundleBid = iterator.next();
            BundleBid trueValuedBundleBid = new BundleBid(bidder.getValue(bundleBid.getBundle()), bundleBid.getBundle(), "TrueValued_" + String.valueOf(auction.getNumberOfRounds()+1) + "_" + bundleBid.getId());
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
