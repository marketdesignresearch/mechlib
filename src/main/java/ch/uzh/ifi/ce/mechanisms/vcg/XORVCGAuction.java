package ch.uzh.ifi.ce.mechanisms.vcg;

import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.mechanisms.winnerdetermination.WinnerDetermination;
import ch.uzh.ifi.ce.mechanisms.winnerdetermination.XORWinnerDetermination;

public class XORVCGAuction extends VCGAuction {

    public XORVCGAuction(AuctionInstance auctionInstance) {
        super(auctionInstance);
    }

    public XORVCGAuction(AuctionInstance auctionInstance, Allocation allocation) {
        super(auctionInstance, allocation);
    }

    @Override
    protected WinnerDetermination getWinnerDetermination() {
        return new XORWinnerDetermination(getAuctionInstance());
    }

    @Override
    protected WinnerDetermination getWinnerDeterminationWithout(Bidder bidder) {
        return new XORWinnerDetermination(getAuctionInstance().without(bidder));
    }

}
