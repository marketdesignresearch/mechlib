package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.Kernel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SupportVectorSetup {

	private final static double DEFAULT_INTERPOLATION_WEIGHT = 100;
	private final static double DEFAULT_INSENSIVITY_THRESHOLD = 0.0001;
	private final static BigDecimal DEFAULT_VALUE_SCALING_FACTOR = BigDecimal.ONE;

	@Getter
	private final double interpolationWeight;
	@Getter
	private final double insensitivityThreshold;
	@Getter
	private final BigDecimal valueScalingFactor;
	@Getter
	private final Kernel kernel;

	public SupportVectorSetup(Kernel kernel) {
		this(DEFAULT_INTERPOLATION_WEIGHT, DEFAULT_INSENSIVITY_THRESHOLD, DEFAULT_VALUE_SCALING_FACTOR, kernel);
	}
}
