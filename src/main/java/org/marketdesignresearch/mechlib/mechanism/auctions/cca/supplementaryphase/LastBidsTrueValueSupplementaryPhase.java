package org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultExactValueQueryInteraction;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class LastBidsTrueValueSupplementaryPhase implements SupplementaryPhase {

    public LastBidsTrueValueSupplementaryPhase() {
    }

    public ExactValueQuery getInteraction(Auction<BundleExactValueBids> auction, Bidder bidder) {
        BundleExactValueBid bid = auction.getLatestAggregatedBids().getBid(bidder);
        if (bid == null) return new DefaultExactValueQueryInteraction(new LinkedHashSet<>(),bidder.getId(),auction);
        Set<Bundle> result = bid.getBundleBids().stream().map(BundleExactValuePair::getBundle).collect(Collectors.toCollection(LinkedHashSet::new));
        return new DefaultExactValueQueryInteraction(result,bidder.getId(),auction);
    }

    @Override
    public String getDescription() {
        return "Supplementary round to submit the true values of the available bids";
    }

	@Override
	public AuctionRoundBuilder<BundleExactValueBids> createNextRoundBuilder(Auction<BundleExactValueBids> auction) {
		return new LastBidsTrueValueSupplementaryRoundBuilder(auction.getDomain().getBidders().stream().collect(Collectors.toMap(b -> b.getId(), b -> this.getInteraction(auction, b),(e1,e2) -> e1, LinkedHashMap::new)),auction);
	}

	@Override
	public boolean phaseFinished(Auction<BundleExactValueBids> auction) {
		return auction.getCurrentPhaseRoundNumber() == 1;
	}

	@Override
	public String getType() {
		return "SUPPLEMENTARY PHASE Bids True Value";
	}
}
