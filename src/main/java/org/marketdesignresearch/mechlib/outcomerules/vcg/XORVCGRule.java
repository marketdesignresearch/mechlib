package org.marketdesignresearch.mechlib.outcomerules.vcg;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

public class XORVCGRule extends BidBasedVCGRule {

	public XORVCGRule(BundleValueBids<?> bids) {
		super(bids);
	}

	@Override
	protected final WinnerDetermination getWinnerDetermination(BundleValueBids<?> bids) {
		return new XORWinnerDetermination(bids);
	}

}
