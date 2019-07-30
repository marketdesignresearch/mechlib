package org.marketdesignresearch.mechlib.outcomerules.vcg;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

public class XORVCGRule extends BidBasedVCGRule {

    public XORVCGRule(Bids bids) {
        super(bids);
    }

    public XORVCGRule(Bids bids, MipInstrumentation mipInstrumentation) {
        super(bids, mipInstrumentation);
    }

    @Override
    protected final WinnerDetermination getWinnerDetermination(Bids bids, MipInstrumentation.MipPurpose purpose) {
        return new XORWinnerDetermination(bids, purpose, this.getMipInstrumentation());
    }

}
