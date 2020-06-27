package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.validator;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.MRPARRefinement;

public class MRPARValidator extends ActivityRuleValidator<MRPARRefinement> {

	@Override
	public void validateRefinement(MRPARRefinement type,
			BundleBoundValueBid activeBids,
			BundleBoundValueBid refinedBids, Prices bidderPrices,
			Bundle provisionalAllocation) throws ValidatorException{

		Bundle candidatePassingTrade = computeCandidatePassingTrade(refinedBids, bidderPrices, provisionalAllocation);

		// Witness Trade
		BigDecimal maxUtility = BigDecimal.ZERO;
		BigDecimal utility;
		Bundle witnessTrade = null;
		for (BundleBoundValuePair bid : refinedBids.getBundleBids()) {
			utility = this.getPerturbedValuation(bid, candidatePassingTrade).subtract(bidderPrices.getPrice(bid.getBundle()).getAmount());
			if (utility.compareTo(maxUtility) > 0) {
				witnessTrade = bid.getBundle();
				maxUtility = utility;
			}
		}
		
		BigDecimal candidateMinUtility = BigDecimal.ZERO;
		if(candidatePassingTrade != null) {
			candidateMinUtility = refinedBids.getBidForBundle(candidatePassingTrade).getLowerBound().subtract(bidderPrices.getPrice(candidatePassingTrade).getAmount());
		}

		if ((witnessTrade == null && candidateMinUtility.compareTo(BigDecimal.ZERO) >= 0) || candidateMinUtility.compareTo(this
				.getPerturbedValuation(refinedBids.getBidForBundle(witnessTrade), candidatePassingTrade).subtract(bidderPrices.getPrice(witnessTrade).getAmount())) >= 0) {
			if ((candidatePassingTrade == null && provisionalAllocation.getTotalAmount() == 0) || (candidatePassingTrade != null && candidatePassingTrade.equals(provisionalAllocation))) {
				return;
			}
			if (provisionalAllocation.getTotalAmount() == 0) {
				return;
			}
			if (candidateMinUtility.compareTo(this
					.getPerturbedValuation(refinedBids.getBidForBundle(provisionalAllocation), candidatePassingTrade).subtract(bidderPrices.getPrice(provisionalAllocation).getAmount())) > 0) {
				return;
			}
		}

		throw new ValidatorException("MRPAR Validation failed");
	}

	public Bundle computeCandidatePassingTrade(BundleValueBid<BundleBoundValuePair> refinedBids,
			Prices bidderPrices, Bundle provisionalAllocation) {
		
		// No trade has 0 utility
		BigDecimal maxUtility = BigDecimal.ZERO;
		BigDecimal utilityLowerBound;
		Set<Bundle> potentialCandidatePassingTrades = new LinkedHashSet<>();
		for (BundleBoundValuePair bid : refinedBids.getBundleBids()) {
			utilityLowerBound = bid.getLowerBound().subtract(bidderPrices.getPrice(bid.getBundle()).getAmount());
			if (utilityLowerBound.compareTo(maxUtility) > 0) {
				maxUtility = utilityLowerBound;
				potentialCandidatePassingTrades = new LinkedHashSet<>();
			}
			if (utilityLowerBound.compareTo(maxUtility) == 0) {
				potentialCandidatePassingTrades.add(bid.getBundle());
			}
		}

		Bundle candidatePassingTrade = null;
		if (!potentialCandidatePassingTrades.isEmpty()) {

			// Breaking ties (i)
			BigDecimal maxUncertainty = BigDecimal.ZERO;
			BigDecimal uncertainty;
			Set<Bundle> potentialCandidatePassingTrades2 = new LinkedHashSet<>();
			for (Bundle b : potentialCandidatePassingTrades) {
				uncertainty = refinedBids.getBidForBundle(b).getSpread();
				if (uncertainty.compareTo(maxUncertainty) > 0) {
					maxUncertainty = uncertainty;
					potentialCandidatePassingTrades2 = new LinkedHashSet<>();
				}
				if (uncertainty.compareTo(maxUncertainty) == 0) {
					potentialCandidatePassingTrades2.add(b);
				}
			}

			// Breaking ties (ii)
			if (potentialCandidatePassingTrades2.contains(provisionalAllocation)) {
				candidatePassingTrade = provisionalAllocation;
			} else {
				candidatePassingTrade = potentialCandidatePassingTrades2.iterator().next();
			}
		}
		return candidatePassingTrade;
	}

	private BigDecimal getPerturbedValuation(BundleBoundValuePair bid, Bundle withRespectTo) {
		if (bid.getBundle().equals(withRespectTo)) {
			return bid.getLowerBound();
		}
		return bid.getUpperBound();
	}
}
