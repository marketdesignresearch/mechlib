package org.marketdesignresearch.mechlib.outcomerules.vcg;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class ORVCGRule extends BidBasedVCGRule {

    public ORVCGRule(BundleValueBids<?> bids) {
        super(bids);
    }

    @Override
    protected final WinnerDetermination getWinnerDetermination(BundleValueBids<?> bids) {
        return new ORWinnerDetermination(bids);
    }

}
