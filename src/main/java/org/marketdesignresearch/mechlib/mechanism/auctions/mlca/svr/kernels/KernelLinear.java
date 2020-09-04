package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import lombok.Getter;
import lombok.Setter;

public class KernelLinear extends Kernel {
	
	@Getter
	@Setter
	private double coeff0;
	@Getter
	@Setter
	private double coeff1;

	public KernelLinear(double coeff0, double coeff1) {
		this.coeff0 = coeff0;
		this.coeff1 = coeff1;
	}

	@Override
	public Double getValue(Bundle bundle, Bundle bundle2) {
		int value = getStandardEncodedValue(bundle, bundle2);
		return coeff0 + coeff1 * value;
	}

	@Override
	protected WinnerDetermination createWinnerDetermination(Domain domain, ElicitationEconomy economy,
			BundleExactValueBids supportVectorsPerBidder, Map<Bidder, Set<Bundle>> excludedBundles) {
		return new WinnerDeterminationLinearKernel(domain, economy, supportVectorsPerBidder, excludedBundles,
				this.getWdpTimeLimit());
	}

	@Override
	public KernelType getKernelType() {
		return KernelType.Linear;
	}
}
