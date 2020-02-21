package org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection.supplementaryround;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.CCAuction;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions.BundleValueTransformableInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions.DefaultExactValueQueryInteraction;

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
    public BundleValueTransformableInteraction<BundleValuePair> getInteraction(CCAuction auction, Bidder bidder) {
        BundleValueBid<BundleValuePair> bid = auction.getLatestAggregatedBids(bidder);
        if (bid == null) return new DefaultExactValueQueryInteraction(new HashSet<>(),bidder.getId());
        Set<Bundle> result = new LinkedHashSet<>();
        int count = 0;
        // TODO: This is not ordered nor unique. If needed, consider storing BundleBids in a List and filtering duplicates
        Iterator<BundleValuePair> iterator = bid.getBundleBids().iterator();
        while (iterator.hasNext() && ++count < numberOfSupplementaryBids) {
        	result.add(iterator.next().getBundle());
        }
        return new DefaultExactValueQueryInteraction(result,bidder.getId());
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
