package org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection.supplementaryround;

import java.util.Iterator;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
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
    public BundleValueBid getSupplementaryBids(CCAuction auction, Bidder bidder) {
        BundleValueBid bid = auction.getLatestAggregatedBids(bidder);
        if (bid == null) return new BundleValueBid();
        BundleValueBid result = new BundleValueBid();
        int count = 0;
        // TODO: This is not ordered nor unique. If needed, consider storing BundleBids in a List and filtering duplicates
        Iterator<BundleValuePair> iterator = bid.getBundleBids().iterator();
        while (iterator.hasNext() && ++count < numberOfSupplementaryBids) {
            BundleValuePair bundleBid = iterator.next();
            BundleValuePair trueValuedBundleBid = new BundleValuePair(bidder.getValue(bundleBid.getBundle()), bundleBid.getBundle(), "TrueValued_" + String.valueOf(auction.getNumberOfRounds()+1) + "_" + bundleBid.getId());
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
