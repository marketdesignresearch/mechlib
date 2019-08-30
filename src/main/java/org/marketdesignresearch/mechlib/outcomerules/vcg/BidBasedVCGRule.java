package org.marketdesignresearch.mechlib.outcomerules.vcg;

import lombok.AccessLevel;
import lombok.Getter;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public abstract class BidBasedVCGRule extends VCGRule {

    @Getter(AccessLevel.PROTECTED)
    private final Bids bids;

    protected BidBasedVCGRule(Bids bids) {
        this.bids = bids;
    }

    @Override
    protected WinnerDetermination getWinnerDetermination() {
        return getWinnerDetermination(getBids());
    }

    @Override
    protected WinnerDetermination getWinnerDeterminationWithout(Bidder bidder) {
        return getWinnerDetermination(getBids().without(bidder));
    }

    protected abstract WinnerDetermination getWinnerDetermination(Bids bids);

}
