package org.marketdesignresearch.mechlib.mechanisms.vcg;

import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

public class XORVCGAuction extends BidBasedVCGAuction {

    public XORVCGAuction(Bids bids) {
        super(bids);
    }

    @Override
    protected final WinnerDetermination getWinnerDetermination(Bids bids) {
        return new XORWinnerDetermination(bids);
    }

}
