package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentationable;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.MachineLearningAllocationInferrer;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.Kernel;

import lombok.Getter;
import lombok.Setter;

public abstract class SupportVector<B extends BundleValueBid<?>, T extends BundleValueBids<B>>
		implements MachineLearningAllocationInferrer, MipInstrumentationable {

	private final BundleExactValueBids supportVectorsPerBider;
	private final Kernel kernel;
	@Getter
	@Setter
	private MipInstrumentation mipInstrumentation;

	@SuppressWarnings("unchecked")
	public SupportVector(SupportVectorSetup setup, T bids, MipInstrumentation mipInstrumentation) {
		this.mipInstrumentation = mipInstrumentation;
		this.supportVectorsPerBider = new BundleExactValueBids();

		for (Map.Entry<Bidder, B> entry : bids.getBidMap().entrySet()) {
			this.supportVectorsPerBider.setBid(entry.getKey(),
					this.createSupportVectorMIP(setup, (B) entry.getValue().multiply(setup.getValueScalingFactor())).getVectors());
		}
		kernel = setup.getKernel();
	}

	@Override
	public Allocation getInferredEfficientAllocation(Domain domain, ElicitationEconomy economy,
			Map<Bidder, Set<Bundle>> excludedBids) {
		return this.kernel.getAllocationWithExcludedBundles(domain, economy, this.supportVectorsPerBider, excludedBids);
	}

	protected abstract SupportVectorMIP<B> createSupportVectorMIP(SupportVectorSetup setup, B bid);
}
