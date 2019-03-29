package ch.uzh.ifi.ce.mechanisms.vcg;

import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.winnerdetermination.WinnerDetermination;

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
