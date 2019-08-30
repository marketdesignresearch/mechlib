package org.marketdesignresearch.mechlib.outcomerules.vcg;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

public class XORVCGRule extends BidBasedVCGRule {

    public XORVCGRule(Bids bids) {
        super(bids);
    }

    @Override
    protected final WinnerDetermination getWinnerDetermination(Bids bids) {
        return new XORWinnerDetermination(bids);
    }

}
