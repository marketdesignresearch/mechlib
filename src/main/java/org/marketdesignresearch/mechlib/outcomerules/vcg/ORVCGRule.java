package org.marketdesignresearch.mechlib.outcomerules.vcg;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class ORVCGRule extends BidBasedVCGRule {

    public ORVCGRule(Bids bids) {
        super(bids);
    }

    @Override
    protected final WinnerDetermination getWinnerDetermination(Bids bids) {
        return new ORWinnerDetermination(bids);
    }

}
