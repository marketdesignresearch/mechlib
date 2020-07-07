package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.util.List;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BidderRefinementRoundInfo {
	@Getter
	public final Set<RefinementType> refinements;
	@Getter
	public final Prices prices;
	@Getter
	public final Allocation alphaAllocation;
	@Getter
	public final ElicitationEconomy refinementEconomy;
	@Getter
	public final List<ElicitationEconomy> economiesToRefineNext;
}