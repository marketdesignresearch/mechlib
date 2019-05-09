package org.marketdesignresearch.mechlib.mechanisms.vcg;

import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class ORVCGAuction extends BidBasedVCGAuction {

    public ORVCGAuction(Bids bids) {
        super(bids);
    }

    @Override
    protected final WinnerDetermination getWinnerDetermination(Bids bids) {
        return new ORWinnerDetermination(bids);
    }

}
