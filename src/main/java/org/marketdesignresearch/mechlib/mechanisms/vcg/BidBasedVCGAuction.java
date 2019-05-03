package org.marketdesignresearch.mechlib.mechanisms.vcg;

import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public abstract class BidBasedVCGAuction extends VCGAuction {

    private final AuctionInstance auctionInstance;

    protected BidBasedVCGAuction(AuctionInstance auctionInstance) {
        this.auctionInstance = auctionInstance;
    }

    protected AuctionInstance getAuctionInstance() {
        return auctionInstance;
    }

    @Override
    protected WinnerDetermination getWinnerDetermination() {
        return getWinnerDetermination(getAuctionInstance());
    }

    @Override
    protected WinnerDetermination getWinnerDeterminationWithout(Bidder bidder) {
        return getWinnerDetermination(getAuctionInstance().without(bidder));
    }

    protected abstract WinnerDetermination getWinnerDetermination(AuctionInstance auctionInstance);

}
