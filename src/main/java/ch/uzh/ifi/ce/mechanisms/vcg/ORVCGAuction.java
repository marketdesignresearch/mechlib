package ch.uzh.ifi.ce.mechanisms.vcg;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.mechanisms.winnerdetermination.ORWinnerDetermination;
import ch.uzh.ifi.ce.mechanisms.winnerdetermination.WinnerDetermination;

public class ORVCGAuction extends VCGAuction {

    public ORVCGAuction(AuctionInstance auctionInstance) {
        super(auctionInstance);
    }

    public ORVCGAuction(AuctionInstance auctionInstance, Allocation allocation) {
        super(auctionInstance, allocation);
    }

    @Override
    protected WinnerDetermination getWinnerDetermination() {
        return new ORWinnerDetermination(getAuctionInstance());
    }

    @Override
    protected WinnerDetermination getWinnerDeterminationWithout(Bidder bidder) {
        return new ORWinnerDetermination(getAuctionInstance().without(bidder));
    }

}
