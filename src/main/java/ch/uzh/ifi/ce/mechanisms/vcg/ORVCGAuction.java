package ch.uzh.ifi.ce.mechanisms.vcg;

import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.winnerdetermination.ORWinnerDetermination;
import ch.uzh.ifi.ce.winnerdetermination.WinnerDetermination;

public class ORVCGAuction extends BidBasedVCGAuction {

    public ORVCGAuction(AuctionInstance auctionInstance) {
        super(auctionInstance);
    }

    @Override
    protected final WinnerDetermination getWinnerDetermination(AuctionInstance auctionInstance) {
        return new ORWinnerDetermination(auctionInstance);
    }

}
