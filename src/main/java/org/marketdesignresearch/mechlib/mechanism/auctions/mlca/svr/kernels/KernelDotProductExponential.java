package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.Map;

import org.marketdesignresearch.mechlib.core.Bundle;

public class KernelDotProductExponential extends KernelDotProduct {
	private double bandwidth;
	private double scalingFactor;

	public KernelDotProductExponential(double bandwidth, double scalingFactor) {
		this.bandwidth = bandwidth;
		this.scalingFactor = scalingFactor;
	}

	public KernelDotProductExponential(Map<String, Double> parameters) {
		this(parameters.get("eb"), parameters.get("es"));
	}

	public Double getValue(Bundle bundle, Bundle bundle2) {
		return scalingFactor * Math.exp(BundleEncoder.getIntersectionSizeWith(bundle, bundle2) / bandwidth);
	}

	public Double getValueGivenDotProduct(int dotProduct) {
		return scalingFactor * Math.exp(dotProduct / bandwidth);
	}

	@Override
	public KernelType getKernelType() {
		return KernelType.Exponential;
	}
}
