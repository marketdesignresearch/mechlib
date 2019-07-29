package org.marketdesignresearch.mechlib.mechanisms.vcg;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

public class XORVCGMechanism extends BidBasedVCGMechanism {

    public XORVCGMechanism(Bids bids) {
        super(bids);
    }

    @Override
    protected final WinnerDetermination getWinnerDetermination(Bids bids) {
        return new XORWinnerDetermination(bids);
    }

}
