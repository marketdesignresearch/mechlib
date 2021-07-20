package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import org.marketdesignresearch.mechlib.core.Bundle;

import lombok.Getter;
import lombok.Setter;

/**
 * An polynomial SVR kernel.
 * 
 * @author Gianluca Brero
 * @author Manuel Beyeler
 */
public class KernelDotProductPolynomial extends KernelDotProduct {

	public static int maxPoly = 10;

	@Getter
	@Setter
	private double[] coefficients;

	public KernelDotProductPolynomial(double[] coefficients) {
		this.coefficients = coefficients;
	}

	public Double getValue(Bundle bundle, Bundle bundle2) {
		Double value = 0.0;
		for (int i = 1; i < coefficients.length; i++) {
			value += coefficients[i] * Math.pow(BundleEncoder.getIntersectionSizeWith(bundle, bundle2), i);
		}
		return value;
	}

	public Double getValueGivenDotProduct(int dotProduct) {
		Double value = 0.0;
		for (int i = 0; i < coefficients.length; i++) {
			value += coefficients[i] * Math.pow(dotProduct, i);
		}
		return value;
	}

	@Override
	public KernelType getKernelType() {
		return KernelType.Polynomial;
	}
}
