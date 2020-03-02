package org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions.CCAExactValueQueryInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class LastBidsTrueValueSupplementaryPhase implements SupplementaryPhase {

    private static final int DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS = 500;

    @Setter @Getter
    private int numberOfSupplementaryBids = DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS;

    public LastBidsTrueValueSupplementaryPhase() {
    }

    public ExactValueQuery getInteraction(Auction<BundleValuePair> auction, Bidder bidder) {
        BundleValueBid<BundleValuePair> bid = auction.getLatestAggregatedBids(bidder);
        if (bid == null) return new CCAExactValueQueryInteraction(new HashSet<>(),bidder.getId(),auction);
        Set<Bundle> result = new LinkedHashSet<>();
        int count = 0;
        // TODO: This is not ordered nor unique. If needed, consider storing BundleBids in a List and filtering duplicates
        Iterator<BundleValuePair> iterator = bid.getBundleBids().iterator();
        while (iterator.hasNext() && ++count < numberOfSupplementaryBids) {
        	result.add(iterator.next().getBundle());
        }
        return new CCAExactValueQueryInteraction(result,bidder.getId(),auction);
    }

    public LastBidsTrueValueSupplementaryPhase withNumberOfSupplementaryBids(int numberOfSupplementaryBids) {
        setNumberOfSupplementaryBids(numberOfSupplementaryBids);
        return this;
    }

    @Override
    public String getDescription() {
        return "Supplementary round to submit the true values of the last " + numberOfSupplementaryBids + " bids";
    }

	@Override
	public AuctionRoundBuilder<BundleValuePair> createNextRoundBuilder(Auction<BundleValuePair> auction) {
		return new LastBidsTrueValueSupplementaryRoundBuilder(auction.getDomain().getBidders().stream().collect(Collectors.toMap(b -> b.getId(), b -> this.getInteraction(auction, b))));
	}

	@Override
	public boolean phaseFinished(Auction<BundleValuePair> auction) {
		return auction.getCurrentPhaseRoundNumber() == 1;
	}

	@Override
	public String getType() {
		return "SUPPLEMENTARY PHASE Profit Max";
	}
}
