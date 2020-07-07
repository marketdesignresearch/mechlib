package org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful;

import java.util.HashMap;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DIARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.MRPARRefinement;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.RefinementType;

/**
 * Interface for automated refiners for one activity rule
 * @author Manuel
 *
 * @param <E>
 */
public abstract class AutomatedRefiner<E extends RefinementType> {
	/**
	 * @param type refinement type
	 * @param b bidder
	 * @param activeBids bids active at the beginning of this refinement round
	 * @param refinedBids bids that might have been refined by a previous called refiner (otherwise equals activeBids) 
	 * @param bidderPrices prices
	 * @param provisionalAllocation provisional allocation of given bidder
	 * @return refined bids
	 */
	public abstract BundleBoundValueBid refineBids(E type, Bidder b,
			BundleBoundValueBid activeBids,
			BundleBoundValueBid refinedBids, Prices prices,
			Bundle provisionalAllocation);
	

	@SuppressWarnings("rawtypes")
	private static Map<Class<? extends RefinementType>,AutomatedRefiner> refiners;
	
	static {
		refiners = new HashMap<>();
		refiners.put(DIARRefinement.class, new DIARRefiner());
		refiners.put(MRPARRefinement.class, new MRPARRefiner());
	}
	
	@SuppressWarnings("unchecked" )
	public static <E extends RefinementType> AutomatedRefiner<E> getRefiner(E type) {
		return refiners.get(type.getClass());
	}
	
	public static BundleBoundValueBid refine(RefinementType type, Bidder bidder, BundleBoundValueBid activeBids, BundleBoundValueBid refinedBids, Prices prices, Bundle provisionalAllocation) {
		return getRefiner(type).refineBids(type, bidder, activeBids, refinedBids, prices, provisionalAllocation);
	}
}
