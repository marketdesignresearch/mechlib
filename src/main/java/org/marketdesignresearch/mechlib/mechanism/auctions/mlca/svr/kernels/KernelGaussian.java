package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class KernelGaussian extends Kernel{
	
	private double bandwidth;
	private double scalingFactor;
	
	public KernelGaussian(Map<String, Double> kernelParameters) {
		this.scalingFactor = kernelParameters.get("gs");
		this.bandwidth = kernelParameters.get("gb");	
	}

	@Override
	public Double getValue(Bundle bundle, Bundle bundle2) {
		int union = BundleEncoder.getUnionSizeWith(bundle, bundle2);
		int diff = BundleEncoder.getIntersectionSizeWith(bundle,bundle2);	
		int numDiffItems = union-diff;
		return scalingFactor*Math.exp(-1/bandwidth*numDiffItems);
	}

	public double getValueGivenDifference(int numDiffItems) {
		return scalingFactor*Math.exp(-1/bandwidth*numDiffItems);
	}

	@Override
	protected WinnerDetermination createWinnerDetermination(Domain domain, ElicitationEconomy economy,
			BundleExactValueBids supportVectorsPerBidder,
			Map<Bidder, Set<Bundle>> excludedBids) {
		return new WinnerDeterminationTranslationInvariantKernel (domain, economy, supportVectorsPerBidder, excludedBids, this);
	}

}
