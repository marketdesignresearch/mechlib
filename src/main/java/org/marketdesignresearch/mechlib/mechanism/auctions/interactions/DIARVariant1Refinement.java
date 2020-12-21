package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DIARVariant1Refinement extends RefinementType{
	@Getter
	private final BigDecimal epsilon;
}
