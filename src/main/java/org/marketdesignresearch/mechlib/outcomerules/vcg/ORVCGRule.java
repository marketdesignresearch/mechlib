package org.marketdesignresearch.mechlib.outcomerules.vcg;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class ORVCGRule extends BidBasedVCGRule {

    public ORVCGRule(Bids bids) {
        super(bids);
    }

    public ORVCGRule(Bids bids, MipInstrumentation mipInstrumentation) {
        super(bids, mipInstrumentation);
    }

    @Override
    protected final WinnerDetermination getWinnerDetermination(Bids bids, MipInstrumentation.MipPurpose purpose) {
        return new ORWinnerDetermination(bids, purpose, getMipInstrumentation());
    }

}
