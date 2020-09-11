package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.phases;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DIARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.MRPARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefinementHelper {
	private RefinementHelper() {
	}

	public static class EfficiencyInfo {
		public BigDecimal alpha;
		public BigDecimal efficiency;
	}

	public static EfficiencyInfo getRefinementInfo(Auction<BundleBoundValueBids> auction, ElicitationEconomy economy) {
		return getRefinementInfo(auction.getLatestAggregatedBids().only(new LinkedHashSet<>(economy.getBidders())));
	}

	public static EfficiencyInfo getRefinementInfo(Auction<BundleBoundValueBids> auction) {
		return getRefinementInfo(auction.getLatestAggregatedBids());
	}

	public static EfficiencyInfo getRefinementInfo(BundleBoundValueBids bids) {
		Allocation lowerBound = new XORWinnerDetermination(bids).getAllocation();
		Allocation perturbed = new XORWinnerDetermination(bids.getPerturbedBids(lowerBound)).getAllocation();

		log.info("Lowerbound Reported Value: " + lowerBound.getTotalAllocationValue().setScale(2, RoundingMode.HALF_UP)
				+ "\tTrue value: " + lowerBound.getTrueSocialWelfare().setScale(2, RoundingMode.HALF_UP));
		log.info("Perturbed Reported Value: " + perturbed.getTotalAllocationValue().setScale(2, RoundingMode.HALF_UP)
				+ "\tTrue value: " + perturbed.getTrueSocialWelfare().setScale(2, RoundingMode.HALF_UP));

		EfficiencyInfo info = new EfficiencyInfo();

		info.alpha = lowerBound.getTotalAllocationValue()
				.divide(lowerBound.getTotalAllocationValue(), RoundingMode.HALF_UP).max(BigDecimal.valueOf(0.5))
				.min(BigDecimal.ONE);

		info.efficiency = lowerBound.getTotalAllocationValue().divide(perturbed.getTotalAllocationValue(),
				RoundingMode.HALF_UP);

		return info;
	}

	public static BigDecimal calulateDIAREpsilon(BundleBoundValueBids bids) {
		BigDecimal epsilon = BigDecimal.ZERO;
		for (Bidder b : bids.getBidders()) {
			BundleBoundValueBid bid = bids.getBid(b);
			for (BundleBoundValuePair value : bid.getBundleBids()) {
				epsilon = epsilon.add(value.getUpperBound().subtract(value.getLowerBound())
						.divide(BigDecimal.valueOf(bid.getBundleBids().size()), RoundingMode.HALF_UP));
			}
		}
		epsilon = epsilon.divide(BigDecimal.valueOf(2 * bids.getBidders().size()), RoundingMode.HALF_UP);
		return epsilon;
	}

	public static Set<RefinementType> getMRPARAndDIAR(BundleBoundValueBids bids) {
		// Linked Hash set - the order of the refinement is deterministic
		Set<RefinementType> refinements = new LinkedHashSet<>();
		refinements.add(new MRPARRefinement());
		refinements.add(new DIARRefinement(calulateDIAREpsilon(bids)));
		return refinements;
	}

	public static Set<RefinementType> getMRPAR() {
		return Set.of(new MRPARRefinement());
	}
}
