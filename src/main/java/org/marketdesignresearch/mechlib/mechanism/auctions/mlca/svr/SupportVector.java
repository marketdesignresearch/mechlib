package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentationable;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.MachineLearningAllocationInferrer;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels.Kernel;

import edu.harvard.econcs.jopt.solver.mip.MIP;
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

		BigDecimal maxValue = bids.getBids().stream().map(BundleValueBid::getBundleBids).flatMap(Set::stream)
				.map(BundleExactValuePair::getAmount).reduce(BigDecimal::max).get();
		BigDecimal maxMipValue = new BigDecimal(MIP.MAX_VALUE).multiply(new BigDecimal(.9));

		BigDecimal scalingFactor = BigDecimal.ONE;
		if (maxValue.compareTo(maxMipValue) > 0) {
			scalingFactor = maxMipValue.divide(maxValue, 10, RoundingMode.HALF_UP);
			if (scalingFactor.compareTo(BigDecimal.ZERO) == 0) {
				throw new IllegalArgumentException("Bids are are too large, scaling will not make sense because"
						+ "it would result in a very imprecise solution. Scaling factor would be smaller than 1e-10.");
			}
		}
		
		for(Map.Entry<Bidder,B> entry : bids.getBidMap().entrySet()) {
			this.supportVectorsPerBider.setBid(entry.getKey(),
					this.createSupportVectorMIP(setup, (B)entry.getValue().multiply(scalingFactor)).getVectors());
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
