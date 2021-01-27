package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import java.math.BigDecimal;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DIARVariant3Refinement extends RefinementType{
	@Getter
	private final BigDecimal epsilon;
	@Getter
	private final Set<Bundle> bundles;
}
