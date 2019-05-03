package org.marketdesignresearch.mechlib.mechanisms.vcg;

import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

public class XORVCGAuction extends BidBasedVCGAuction {

    public XORVCGAuction(AuctionInstance auctionInstance) {
        super(auctionInstance);
    }

    @Override
    protected final WinnerDetermination getWinnerDetermination(AuctionInstance auctionInstance) {
        return new XORWinnerDetermination(auctionInstance);
    }

}
