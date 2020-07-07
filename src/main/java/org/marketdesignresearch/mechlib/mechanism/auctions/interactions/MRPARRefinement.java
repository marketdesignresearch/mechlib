package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

/**
 * Asks a bidder to perform a MRPAR refinement
 * 
 * @author Manuel
 *
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
