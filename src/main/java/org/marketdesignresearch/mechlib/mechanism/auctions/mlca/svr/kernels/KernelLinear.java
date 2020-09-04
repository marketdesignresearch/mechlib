package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class KernelLinear extends Kernel {

	private double coeff0;
	private double coeff1;

	public KernelLinear(double coeff0, double coeff1) {
		this.coeff0 = coeff0;
		this.coeff1 = coeff1;
	}

	public KernelLinear(Map<String, Double> kernelParameters) {
		this.coeff0 = kernelParameters.get("p0");
		this.coeff1 = kernelParameters.get("p1");
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
