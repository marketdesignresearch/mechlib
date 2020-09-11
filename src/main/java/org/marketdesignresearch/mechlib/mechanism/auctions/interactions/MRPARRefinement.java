package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

/**
 * Asks a bidder to perform a MRPAR refinement
 * 
 * MRPAR by Lubin et. al. (2008).
 * 
 * @author Manuel Beyeler
 */
public final class MRPARRefinement extends RefinementType {

	@Override
	public boolean equals(Object o) {
		return o instanceof MRPARRefinement;
	}

	@Override
	public int hashCode() {
		return 1;
	}
}
