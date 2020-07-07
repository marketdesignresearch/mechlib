package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.Bundle;

public class KernelDotProductPolynomial extends KernelDotProduct {

	public static int maxPoly = 10;

	private double[] coefficients;

	public KernelDotProductPolynomial(double[] coefficients) {
		this.coefficients = coefficients;
	}

	public KernelDotProductPolynomial(Map<String, Double> kernelParameters) {
		List<Double> coefficientList = new ArrayList<>();
		for (int i = 0; i < maxPoly; i++)
			if (kernelParameters.containsKey("p" + i))
				coefficientList.add(kernelParameters.get("p" + i));
			else
				break;
		this.coefficients = new double[coefficientList.size()];
		for (int i = 0; i < coefficientList.size(); i++) {
			this.coefficients[i] = coefficientList.get(i);
		}

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
}
