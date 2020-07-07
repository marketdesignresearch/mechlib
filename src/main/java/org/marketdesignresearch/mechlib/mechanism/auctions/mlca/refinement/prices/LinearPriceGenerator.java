package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LinearPriceGenerator {

	public static Prices getPrices(Domain domain, ElicitationEconomy setting,
			Allocation allocation, List<BundleExactValueBids> valuations, boolean minimizeAllDeltas) {
		
		Iterator<BundleExactValueBids> it = valuations.iterator();
		
		BigDecimal offset = BigDecimal.valueOf(1e-5);
		PriceConstraints constraint = new PriceConstraints(setting.getBidders());
		Prices prices = null;
		// generate prices according to different targets (valuations)
		while(it.hasNext()) {
			BundleExactValueBids currentValuation = it.next();
			// make sure the empty bid is present
			currentValuation.getBidMap().values().forEach(b -> b.addBundleBid(new BundleExactValuePair(BigDecimal.ZERO, Bundle.EMPTY, UUID.randomUUID().toString())));
			boolean fixNegativeDeltas = !it.hasNext() && minimizeAllDeltas;
			
			// minimize overall delta
			LinearPriceMinimizeDeltaMIP accMip = new LinearPriceMinimizeDeltaMIP(domain, setting.getBidders(), currentValuation, allocation, constraint);
			prices = accMip.getPrices();
			
			BigDecimal localOffset = offset;
			boolean done = false;
			do {
				try {
					// find all deltas that can not be set negative
					// only if positive deltas exist (with some slack to avoid infeasibility of following mips)
					Map<Bidder,Set<Bundle>> positiveDeltas = setting.getBidders().stream().map(b -> domain.getBidders().stream().filter(bidder -> bidder.getId().equals(b)).findAny().orElseThrow()).collect(Collectors.toMap(b->b, b->new HashSet<Bundle>()));
					if(accMip.getDeltaResult().compareTo(localOffset.negate()) > 0) {
						LinearPriceMinimizeNumberOfPositiveDeltasMIP posMip = new LinearPriceMinimizeNumberOfPositiveDeltasMIP(domain, setting.getBidders(), currentValuation, allocation, constraint, accMip.getDeltaResult(), localOffset);
						prices = posMip.getPrices();
						positiveDeltas = posMip.getPositiveDeltas();
					}
					
					if(positiveDeltas.values().stream().mapToInt(Set::size).sum() > 0 || fixNegativeDeltas) {
						LinearPriceMinimizeDeltasWithNorm normDelta = new LinearPriceMinimizeDeltasWithNorm(domain, setting.getBidders(), currentValuation, allocation, constraint, accMip.getDeltaResult(), localOffset, fixNegativeDeltas, positiveDeltas);
						prices = normDelta.getPrices();
						constraint = normDelta.getGeneratedPriceConstraints();
					} else {
						// just update constraints to match new optimization goal (i.e. prices must be clearing prices also with respect to current valuation)
						constraint = new PriceConstraints(domain, setting.getBidders(), currentValuation, allocation, constraint);
					}
					
					done = true;
				}
				catch(RuntimeException re) {
					localOffset = localOffset.scaleByPowerOfTen(1);
					log.warn("Increasing offset due to infeasibility. New offset: {}", offset,re);
				}
			} while(localOffset.compareTo(BigDecimal.valueOf(100))<0 && !done);
			
			// increase basic offset for next valuation
			offset = offset.scaleByPowerOfTen(1);
		}
		
		
		try {
			// Maximize prices
			LinearPriceMaximizePricesMIP max = new LinearPriceMaximizePricesMIP(domain, setting.getBidders(), allocation, constraint);
			prices = max.getPrices();
			
			// Make prices unique
			LinearPriceMinimizePricesNormMIP euclid = new LinearPriceMinimizePricesNormMIP(domain, setting.getBidders(), allocation, constraint, max.getPriceSum());
			prices = euclid.getPrices();
		} catch(RuntimeException e) {
			log.warn("Unable to maximize prices or minimize euclidean distance", e);
		}
		
		return prices;
	}
}
