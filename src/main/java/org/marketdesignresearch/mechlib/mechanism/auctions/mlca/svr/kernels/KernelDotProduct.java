package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public abstract class KernelDotProduct extends Kernel {

	public abstract Double getValue(Bundle bundle, Bundle bundle2);

	public abstract Double getValueGivenDotProduct(int i);

	@Override
	protected WinnerDetermination createWinnerDetermination(Domain domain, ElicitationEconomy economy,
			BundleExactValueBids supportVectorsPerBidder, Map<Bidder, Set<Bundle>> excludedBids) {
		return new WinnerDeterminationDotProductKernel(domain, economy, supportVectorsPerBidder, excludedBids, this,
				this.getWdpTimeLimit());
	}
}
