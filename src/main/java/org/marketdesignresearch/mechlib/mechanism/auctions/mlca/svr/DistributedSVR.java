package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentationable;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.MachineLearningComponent;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.Kernel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Default machine learning component for MLCA based on support vector
 * regression (SVR)
 * 
 * @author Manuel Beyeler
 *
 * @param <T> the type of bids supported by this SVR implementation
 */
@RequiredArgsConstructor
public abstract class DistributedSVR<T extends BundleValueBids<?>>
		implements MachineLearningComponent<T>, MipInstrumentationable {

	@Getter
	private final SupportVectorSetup setup;

	@Getter
	@Setter
	private MipInstrumentation mipInstrumentation = MipInstrumentation.NO_OP;

	public DistributedSVR(Kernel kernel) {
		this(new SupportVectorSetup(kernel));
	}
}
