package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.Map;

import org.marketdesignresearch.mechlib.core.Bundle;

import lombok.Getter;
import lombok.Setter;

/**
 * An exponential SVR kernel.
 * 
 * @author Gianluca Brero
 * @author Manuel Beyeler
 */
public class KernelDotProductExponential extends KernelDotProduct {

	@Getter
	@Setter
	private double bandwidth;
	@Getter
	@Setter
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
