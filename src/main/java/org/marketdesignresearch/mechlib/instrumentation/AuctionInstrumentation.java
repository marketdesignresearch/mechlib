package org.marketdesignresearch.mechlib.instrumentation;

import lombok.EqualsAndHashCode;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;

@EqualsAndHashCode
public class AuctionInstrumentation {
    public void preAuction(Auction auction) {}
    public void postRound(AuctionRound auctionRound) {}
}
