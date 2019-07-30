package org.marketdesignresearch.mechlib.instrumentation;

import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRound;

public class AuctionInstrumentation {
    public void preAuction(Auction auction) {}
    public void postRound(AuctionRound auctionRound) {}
}
