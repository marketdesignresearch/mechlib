package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.Kernel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SupportVectorSetup {
	
	@Getter
	private final double interpolationWeight;
	@Getter
	private final double insensitivityThreshold;
	@Getter
	private final Kernel kernel;
}
