package org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.MRPARRefinement;

/**
 * Automated refiner that performs an MRPAR refinement
 * 
 * @author Manuel
 */
public class MRPARRefiner extends AutomatedRefiner<MRPARRefinement> {

	/**
	 * Used to show a strict preference of the preferred bundle over the provisional allocation if they are not equal 
	 */
	private static BigDecimal strictEta = BigDecimal.valueOf(1e-4);
	
	/**
	 * Even though working with BigDecimal arithmetics some slack
	 * is needed to avoid validation errors later
	 */
	private static BigDecimal localStrictDelta = BigDecimal.valueOf(1E-10d); 
	
	/**
	 * Sums all refinement performed by any MRPARRefiner during this program execution
	 */
	public static BigDecimal overallRefinement = BigDecimal.ZERO;
	
	private static synchronized void addAmount(BigDecimal amount) {
		overallRefinement = overallRefinement.add(amount);
	}
	
	@Override
	// see thesis for details
	public BundleBoundValueBid refineBids(MRPARRefinement type, Bidder b,
			BundleBoundValueBid activeBids,
			BundleBoundValueBid refinedBids, Prices prices,
			Bundle provisionalAllocation, Random random) {
		
		BundleBoundValueBid returnBid = refinedBids.copy();
		
		ImmutablePair<Bundle, BigDecimal> prefered = computePreferedBundle(b, activeBids, prices, provisionalAllocation);
		
		BigDecimal secondBestUtility = BigDecimal.valueOf(-Double.MAX_VALUE);
		BigDecimal secondBestUpperBoundUtility = BigDecimal.valueOf(-Double.MAX_VALUE);

		// find second best true utility and upper bound utility
		for (BundleBoundValuePair bid : activeBids.getBundleBids()) {
			if(!bid.getBundle().equals(prefered.left)) {
				BigDecimal utility = b.getValue(bid.getBundle()).subtract(prices.getPrice(bid.getBundle()).getAmount());
				BigDecimal upperBoundUtility = refinedBids.getBidForBundle(bid.getBundle()).getUpperBound().subtract(prices.getPrice(bid.getBundle()).getAmount());
				if (utility.compareTo(secondBestUtility) > 0) {
					secondBestUtility = utility;
				}
				if(upperBoundUtility.compareTo(secondBestUpperBoundUtility) > 0) {
					secondBestUpperBoundUtility = upperBoundUtility;
				}
			}
		}
		
		// check for empty bundle if this is not the preferred one 
		if(prefered.left != null && !prefered.left.equals(Bundle.EMPTY)) {
			secondBestUtility = BigDecimal.ZERO.max(secondBestUtility);
			secondBestUpperBoundUtility = BigDecimal.ZERO.max(secondBestUpperBoundUtility);
		}
		
		BigDecimal breakpointUtility = secondBestUtility.add(prefered.right.subtract(secondBestUtility).multiply(BigDecimal.valueOf(this.getNextGuassianLikeDouble(b, random))));
		
		BigDecimal preferedLowerBoundUtility = BigDecimal.ZERO;
		if(prefered.left != null) {
			preferedLowerBoundUtility = refinedBids.getBidForBundle(prefered.left).getLowerBound().subtract(prices.getPrice(prefered.left).getAmount()); 
		}
		
		// Do not refine more than needed
		breakpointUtility = breakpointUtility.max(preferedLowerBoundUtility);
		breakpointUtility = breakpointUtility.min(secondBestUpperBoundUtility);
		
		// Make sure the strictness of MRPAR holds for candidate passing trade <> provisional trade
		// even if local delta is set to 0
		if(prefered.left != null && !prefered.left.equals(provisionalAllocation)) {
			localStrictDelta = localStrictDelta.add(strictEta);
		} else if(provisionalAllocation != null && !provisionalAllocation.equals(prefered.left)) {
			localStrictDelta = localStrictDelta.add(strictEta);
		}

		if (prefered.left != null) {
			BigDecimal newMin = breakpointUtility.add(prices.getPrice(prefered.left).getAmount()).add(localStrictDelta);
			// Do not lower the min even if breakpointUtility would suggest so
			newMin = newMin.max(refinedBids.getBidForBundle(prefered.left).getLowerBound());
			// Make sure bids are still truthful
			newMin = newMin.min(b.getValue(prefered.left));
		
			MRPARRefiner.addAmount(newMin.subtract(refinedBids.getBidForBundle(prefered.left).getLowerBound()));
			returnBid.addBundleBid(new BundleBoundValuePair(newMin, refinedBids.getBidForBundle(prefered.left).getUpperBound(), prefered.left, UUID.randomUUID().toString()));
		}


		// find bundles that may need to be refined (lower the upper bounds)
		Set<Bundle> witnessTrades = this.getWitnessTrades(prefered.left, refinedBids, prices);
		
		for (Bundle bundle : witnessTrades) {
			BigDecimal delta = localStrictDelta;
			if (bundle.equals(provisionalAllocation)) {
				delta = delta.add(strictEta);
			}

			BigDecimal newMax = refinedBids.getBidForBundle(bundle).getUpperBound().min(breakpointUtility.add(prices.getPrice(bundle).getAmount()).subtract(delta));
			// do no increase max even if breakpointUtility is higher
			newMax = newMax.min(refinedBids.getBidForBundle(bundle).getUpperBound());
			// Make sure bids are still truthful
			newMax = newMax.max(b.getValue(bundle));
			
			MRPARRefiner.addAmount(refinedBids.getBidForBundle(bundle).getUpperBound().subtract(newMax));
			returnBid.addBundleBid(new BundleBoundValuePair(refinedBids.getBidForBundle(bundle).getLowerBound(), newMax, bundle, UUID.randomUUID().toString()));
		}

		return returnBid;
	}

	public ImmutablePair<Bundle, BigDecimal> computePreferedBundle(Bidder b, BundleValueBid<BundleBoundValuePair> activeBids,
			Prices prices, Bundle provisionalAllocation) {
		Bundle preferedBundle = null;
		BigDecimal preferedUtility = BigDecimal.ZERO;

		// find one bundle with the highest utility
		for (BundleBoundValuePair pair : activeBids.getBundleBids()) {
			BigDecimal utility = b.getValue(pair.getBundle()).subtract(prices.getPrice(pair.getBundle()).getAmount());
			if (utility.compareTo(preferedUtility) > 0) {
				preferedBundle = pair.getBundle();
				preferedUtility = utility;
			}
		}
		
		// break ties in preference for the provisional allocation, so MRPAR can be satisfied
		if(!(provisionalAllocation.getTotalAmount() == 0) && b.getValue(provisionalAllocation).subtract(prices.getPrice(provisionalAllocation).getAmount()).compareTo(preferedUtility) >= 0) {
			preferedBundle = provisionalAllocation;
		}
		return new ImmutablePair<>(preferedBundle, preferedUtility);
	}


	private Set<Bundle> getWitnessTrades(Bundle preferedBundle, BundleValueBid<BundleBoundValuePair> refinedBids,
			Prices prices) {

		BigDecimal minUtilityPreferedBundle = BigDecimal.ZERO;
		if (preferedBundle != null) {
			minUtilityPreferedBundle = refinedBids.getBidForBundle(preferedBundle).getLowerBound().subtract(prices.getPrice(preferedBundle).getAmount());
		}
		Set<Bundle> witnessTrades = new LinkedHashSet<>();

		for (BundleBoundValuePair bid : refinedBids.getBundleBids()) {
			if (!bid.getBundle().equals(preferedBundle)) {
				if (bid.getUpperBound().subtract(prices.getPrice(bid.getBundle()).getAmount()).compareTo(minUtilityPreferedBundle) >= 0) {
					witnessTrades.add(bid.getBundle());
				}
			}
		}
		return witnessTrades;
	}
}
