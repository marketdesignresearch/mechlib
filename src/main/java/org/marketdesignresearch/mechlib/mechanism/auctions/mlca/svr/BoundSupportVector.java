package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;

import edu.harvard.econcs.jopt.solver.mip.MIP;

public class BoundSupportVector extends SupportVector<BundleBoundValueBid,BundleBoundValueBids>{

	public BoundSupportVector(SupportVectorSetup setup, BundleBoundValueBids bids, MipInstrumentation mipInstrumentation) {
		super(setup, (BundleBoundValueBids) bids.multiply(
				(bids.getBids().stream().map(BundleBoundValueBid::getBundleBids).flatMap(Set::stream)
				.map(BundleBoundValuePair::getUpperBound).reduce(BigDecimal::max).get().compareTo(BigDecimal.valueOf(MIP.MAX_VALUE).multiply(BigDecimal.valueOf(.9)))
				> 0 ? 
						BigDecimal.valueOf(MIP.MAX_VALUE).multiply(BigDecimal.valueOf(.88)).divide(bids.getBids().stream().map(BundleBoundValueBid::getBundleBids).flatMap(Set::stream)
				.map(BundleBoundValuePair::getUpperBound).reduce(BigDecimal::max).get(),10,RoundingMode.HALF_UP)
						
						: BigDecimal.ONE)
				), mipInstrumentation);
	}

	@Override
	protected SupportVectorMIP<BundleBoundValueBid> createSupportVectorMIP(SupportVectorSetup setup,
			BundleBoundValueBid bid) {
		return new BoundSupportVectorMIP(setup, bid, this.getMipInstrumentation());
	}

}
