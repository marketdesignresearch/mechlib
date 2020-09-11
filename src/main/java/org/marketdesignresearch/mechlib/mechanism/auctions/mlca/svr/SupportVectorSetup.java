package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.Kernel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The support vector setup (i.e. hyperparametes for the SVRs). Note that this version is not
 * normalized. Default setups for some Kernels for GSVM, LSVM and MRVM domain are 
 * part of SATS where also these domains are defined.
 * 
 * @author Manuel Beyeler
 */
@AllArgsConstructor
public class SupportVectorSetup {

	private final static double DEFAULT_INTERPOLATION_WEIGHT = 100;
	private final static double DEFAULT_INSENSIVITY_THRESHOLD = 0.0001;
	private final static BigDecimal DEFAULT_VALUE_SCALING_FACTOR = BigDecimal.ONE;
	private final static boolean DEFAULT_REMOVE_SMALL_SUPPORT_VECTORS = true;

	/**
	 * Parameter C from Brero et. al. (2020), i.e. regularization trade-off
	 */
	@Getter
	@Setter
	private  double interpolationWeight;
	/**
	 * Parameter epsilon from Brero et. al. (2020), i.e. the epsilon from the epsilon-insensivity loss function
	 */
	@Getter
	@Setter
	private double insensitivityThreshold;
	/**
	 * In some cases the values of a domain might be too high (or low) to be handled properly by this SVR 
	 * formulation. Therefore you might scale them (value * valueScalingFactor) by a fixed amount such that 
	 * you still have full control over all hyperparamters
	 */
	@Getter
	@Setter
	private BigDecimal valueScalingFactor;
	/**
	 * For performance reasons support vectors below 1e-5 may be removed/cancelled after the SVR training.
	 * Set this parameter to false if you want to avoid this.
	 */
	@Getter
	@Setter
	private boolean removeSmallSupportVectors;
	/**
	 * The SVR kernel
	 */
	@Getter
	@Setter
	private Kernel kernel;

	public SupportVectorSetup(Kernel kernel) {
		this(DEFAULT_INTERPOLATION_WEIGHT, DEFAULT_INSENSIVITY_THRESHOLD, DEFAULT_VALUE_SCALING_FACTOR, DEFAULT_REMOVE_SMALL_SUPPORT_VECTORS, kernel);
	}
}
