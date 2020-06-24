package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

public class KernelQuadratic extends Kernel{
	
	private double coeff0;
	private double coeff1;
	private double coeff2;
	
	public KernelQuadratic (double coeff0, double coeff1, double coeff2) {
		this.coeff0=coeff0;
		this.coeff1=coeff1;
		this.coeff2=coeff2;		
	}

	public KernelQuadratic(Map<String, Double> kernelParameters) {
		this.coeff0 = kernelParameters.get("p0");
		this.coeff1 = kernelParameters.get("p1");
		this.coeff2 = kernelParameters.get("p2");
	}

	@Override
	public Double getValue(Bundle bundle, Bundle bundle2) {
		int value = this.getStandardEncodedValue(bundle, bundle2);
		return coeff0 + coeff1 * value + coeff2 * Math.pow(value,2);
	}

	public double getDeg0Coeff() {
		return coeff0;	
	}

	public double getDeg1Coeff() {
		return coeff1;	
	}

	public double getDeg2Coeff() {
		return coeff2;	
	}

	@Override
	protected WinnerDetermination createWinnerDetermination(Domain domain, ElicitationEconomy economy,
			BundleExactValueBids supportVectorsPerBidder, Map<Bidder,Set<Bundle>> excludedBundles) {
		return new WinnerDeterminationQuadraticKernel (domain, economy, supportVectorsPerBidder, excludedBundles, this, this.getWdpTimeLimit());
	}


}
