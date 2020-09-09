package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.Kernel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class SupportVectorSetup {

	private final static double DEFAULT_INTERPOLATION_WEIGHT = 100;
	private final static double DEFAULT_INSENSIVITY_THRESHOLD = 0.0001;
	private final static BigDecimal DEFAULT_VALUE_SCALING_FACTOR = BigDecimal.ONE;
	private final static boolean DEFAULT_REMOVE_SMALL_SUPPORT_VECTORS = true;

	@Getter
	@Setter
	private  double interpolationWeight;
	@Getter
	@Setter
	private double insensitivityThreshold;
	@Getter
	@Setter
	private BigDecimal valueScalingFactor;
	@Getter
	@Setter
	private boolean removeSmallSupportVectors;
	@Getter
	@Setter
	private Kernel kernel;

	public SupportVectorSetup(Kernel kernel) {
		this(DEFAULT_INTERPOLATION_WEIGHT, DEFAULT_INSENSIVITY_THRESHOLD, DEFAULT_VALUE_SCALING_FACTOR, DEFAULT_REMOVE_SMALL_SUPPORT_VECTORS, kernel);
	}
}
