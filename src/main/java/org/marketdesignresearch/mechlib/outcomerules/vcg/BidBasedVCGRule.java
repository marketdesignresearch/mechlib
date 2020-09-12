package org.marketdesignresearch.mechlib.outcomerules.vcg;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import lombok.AccessLevel;
import lombok.Getter;

public abstract class BidBasedVCGRule extends VCGRule {

	@Getter(AccessLevel.PROTECTED)
	private final BundleValueBids<?> bids;

	protected BidBasedVCGRule(BundleValueBids<?> bids) {
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

	protected abstract WinnerDetermination getWinnerDetermination(BundleValueBids<?> bids);

}
