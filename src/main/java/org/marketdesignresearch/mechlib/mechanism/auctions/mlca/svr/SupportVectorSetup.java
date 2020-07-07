package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.Kernel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SupportVectorSetup {

	private final static double DEFAULT_INTERPOLATION_WEIGHT = 100;
	private final static double DEFAULT_INSENSIVITY_THRESHOLD = 0.0001;

	@Getter
	private final double interpolationWeight;
	@Getter
	private final double insensitivityThreshold;
	@Getter
	private final Kernel kernel;

	public SupportVectorSetup(Kernel kernel) {
		this(DEFAULT_INTERPOLATION_WEIGHT, DEFAULT_INSENSIVITY_THRESHOLD, kernel);
	}
}
