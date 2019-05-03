package org.marketdesignresearch.mechlib.mechanisms.vcg;

import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class ORVCGAuction extends BidBasedVCGAuction {

    public ORVCGAuction(AuctionInstance auctionInstance) {
        super(auctionInstance);
    }

    @Override
    protected final WinnerDetermination getWinnerDetermination(AuctionInstance auctionInstance) {
        return new ORWinnerDetermination(auctionInstance);
    }

}
