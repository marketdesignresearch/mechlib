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

/**
 * A gaussian SVR kernel.
 * 
 * @author Gianluca Brero
 * @author Manuel Beyeler
 */
public class KernelGaussian extends Kernel {

	@Getter
	@Setter
	private double bandwidth;
	@Getter
	@Setter
	private double scalingFactor;

	public KernelGaussian(double bandwidth, double scalingFactor) {
		this.bandwidth = bandwidth;
		this.scalingFactor = scalingFactor;
	}

	@Override
	public Double getValue(Bundle bundle, Bundle bundle2) {
		int union = BundleEncoder.getUnionSizeWith(bundle, bundle2);
		int diff = BundleEncoder.getIntersectionSizeWith(bundle, bundle2);
		int numDiffItems = union - diff;
		return scalingFactor * Math.exp(-1 / bandwidth * numDiffItems);
	}

	public double getValueGivenDifference(int numDiffItems) {
		return scalingFactor * Math.exp(-1 / bandwidth * numDiffItems);
	}

	@Override
	protected WinnerDetermination createWinnerDetermination(Domain domain, ElicitationEconomy economy,
			BundleExactValueBids supportVectorsPerBidder, Map<Bidder, Set<Bundle>> excludedBids) {
		return new WinnerDeterminationTranslationInvariantKernel(domain, economy, supportVectorsPerBidder, excludedBids,
				this, this.getWdpTimeLimit());
	}

	@Override
	public KernelType getKernelType() {
		return KernelType.Gaussian;
	}

}
