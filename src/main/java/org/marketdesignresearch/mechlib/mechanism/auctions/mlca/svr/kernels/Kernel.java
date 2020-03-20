package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public abstract class Kernel {
	
	public abstract Double getValue(Bundle bundle, Bundle bundle2);

	public Allocation getAllocationWithExcludedBundles(Domain domain, ElicitationEconomy economy,
			BundleExactValueBids supportVectorsPerBidder, Map<Bidder,Set<Bundle>> excludedBundles) {
		WinnerDetermination wd = this.createWinnerDetermination(domain, economy, supportVectorsPerBidder, excludedBundles);
		return wd.getAllocation();
	}
	
	protected abstract WinnerDetermination createWinnerDetermination(Domain domain, ElicitationEconomy economy,
			BundleExactValueBids supportVectorsPerBidder, Map<Bidder,Set<Bundle>> excludedBids);

	protected int getStandardEncodedValue(Bundle bundle, Bundle bundle2) {
		return BundleEncoder.getStandardDotProdWith(bundle, bundle2);
	}
}
