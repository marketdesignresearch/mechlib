package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.svr.kernels;

import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import lombok.Getter;
import lombok.Setter;

/**
 * A quadratic SVR kernel.
 * 
 * @author Gianluca Brero
 * @author Manuel Beyeler
 */
public class KernelQuadratic extends Kernel {
	
	@Getter
	@Setter
	private double coeff0;
	@Getter
	@Setter
	private double coeff1;
	@Getter
	@Setter
	private double coeff2;

	public KernelQuadratic(double coeff0, double coeff1, double coeff2) {
		this.coeff0 = coeff0;
		this.coeff1 = coeff1;
		this.coeff2 = coeff2;
	}
	
	@Override
	public Double getValue(Bundle bundle, Bundle bundle2) {
		int value = BundleEncoder.getStandardDotProdWith(bundle, bundle2);
		return coeff0 + coeff1 * value + coeff2 * Math.pow(value, 2);
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
			BundleExactValueBids supportVectorsPerBidder, Map<Bidder, Set<Bundle>> excludedBundles) {
		return new WinnerDeterminationQuadraticKernel(domain, economy, supportVectorsPerBidder, excludedBundles, this,
				this.getWdpTimeLimit());
	}

	@Override
	public KernelType getKernelType() {
		return KernelType.Quadratic;
	}
	
}
