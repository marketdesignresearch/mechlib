package ch.uzh.ifi.ce.mechanisms.vcg;

import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.winnerdetermination.WinnerDetermination;
import ch.uzh.ifi.ce.winnerdetermination.XORWinnerDetermination;

public class XORVCGAuction extends BidBasedVCGAuction {

    public XORVCGAuction(AuctionInstance auctionInstance) {
        super(auctionInstance);
    }

    @Override
    protected WinnerDetermination getWinnerDetermination(AuctionInstance auctionInstance) {
        return new XORWinnerDetermination(auctionInstance);
    }

}
