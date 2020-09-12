package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Asks a bidder to perform a DIAR refinement with the given epsilon
 * 
 * DIAR by Lubin et. al. (2008).
 * 
 * @author Manuel Beyeler
 */
@RequiredArgsConstructor
public final class DIARRefinement extends RefinementType {

	@Getter
	private final BigDecimal epsilon;
}
