package org.marketdesignresearch.mechlib.mechanism.auctions.mlca.refinement.prices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.mlca.ElicitationEconomy;

import edu.harvard.econcs.jopt.solver.mip.MIP;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LinearPriceGenerator {

	@Setter
	private double timeLimit = -1.0;

	public LinearPriceGenerator() {
	}

	public Prices getPrices(Domain domain, ElicitationEconomy setting, Allocation allocation,
			List<BundleExactValueBids> valuations, boolean minimizeAllDeltas) {

		BigDecimal maxValue = valuations.stream().flatMap(b -> b.getBids().stream())
				.map(BundleExactValueBid::getBundleBids).flatMap(Set::stream).map(BundleExactValuePair::getAmount)
				.reduce(BigDecimal::max).get();
		BigDecimal maxMipValue = BigDecimal.valueOf(MIP.MAX_VALUE).multiply(BigDecimal.valueOf(.7));

		BigDecimal scalingFactor = BigDecimal.ONE;
		if (maxValue.compareTo(maxMipValue) > 0) {
			scalingFactor = maxMipValue.divide(maxValue, 10, RoundingMode.HALF_UP);
			if (scalingFactor.compareTo(BigDecimal.ZERO) == 0) {
				throw new IllegalArgumentException("Bids are are too large, scaling will not make sense because"
						+ "it would result in a very imprecise solution. Scaling factor would be smaller than 1e-10.");
			}
		}

		List<BundleExactValueBids> scaledValues = new ArrayList<>();
		for (BundleExactValueBids bids : valuations) {
			scaledValues.add(bids.multiply(scalingFactor));
		}

		Iterator<BundleExactValueBids> it = scaledValues.iterator();

		BigDecimal offset = BigDecimal.valueOf(1e-5);
		PriceConstraints constraint = new PriceConstraints(setting.getBidders());
		Prices prices = null;
		// generate prices according to different targets (valuations)
		while (it.hasNext()) {
			BundleExactValueBids currentValuation = it.next();
			// make sure the empty bid is present
			currentValuation.getBidMap().values().forEach(b -> b.addBundleBid(
					new BundleExactValuePair(BigDecimal.ZERO, Bundle.EMPTY, UUID.randomUUID().toString())));
			boolean fixNegativeDeltas = !it.hasNext() && minimizeAllDeltas;

			BigDecimal localOffset = offset;
			boolean done = false;
			LinearPriceMinimizeDeltaMIP accMip = null;
			do {
				try {
					// minimize overall delta
					accMip = new LinearPriceMinimizeDeltaMIP(domain, setting.getBidders(), currentValuation, allocation,
							constraint, timeLimit);
					prices = accMip.getPrices();
					done = true;
				} catch (RuntimeException re) {
					constraint.addSlack(localOffset);
					log.warn("Unable to solve LinearPriceMinimizeDeltaMIP. Add more Slack to constraints: {}",
							localOffset, re);
					localOffset = localOffset.scaleByPowerOfTen(1);
					// happens rarely that such a high offset needs to be added,
					// but for domains with high values it might occur
					// continue anyway
					if (localOffset.compareTo(BigDecimal.valueOf(100)) > 0) {
						done = true;
					}
				}
			} while (!done);

			localOffset = offset;
			done = false;
			do {
				try {
					// find all deltas that can not be set negative
					// only if positive deltas exist (with some slack to avoid infeasibility of
					// following mips)
					Map<Bidder, Set<Bundle>> positiveDeltas = setting.getBidders().stream()
							.map(b -> domain.getBidders().stream().filter(bidder -> bidder.getId().equals(b)).findAny()
									.orElseThrow())
							.collect(Collectors.toMap(b -> b, b -> new LinkedHashSet<Bundle>(), (e1, e2) -> e1,
									LinkedHashMap::new));
					if (accMip.getDeltaResult().compareTo(localOffset.negate()) > 0) {
						LinearPriceMinimizeNumberOfPositiveDeltasMIP posMip = new LinearPriceMinimizeNumberOfPositiveDeltasMIP(
								domain, setting.getBidders(), currentValuation, allocation, constraint,
								accMip.getDeltaResult(), localOffset, timeLimit);
						prices = posMip.getPrices();
						positiveDeltas = posMip.getPositiveDeltas();
					}

					if (positiveDeltas.values().stream().mapToInt(Set::size).sum() > 0 || fixNegativeDeltas) {
						LinearPriceMinimizeDeltasWithNorm normDelta = new LinearPriceMinimizeDeltasWithNorm(domain,
								setting.getBidders(), currentValuation, allocation, constraint, accMip.getDeltaResult(),
								localOffset, fixNegativeDeltas, positiveDeltas, timeLimit);
						prices = normDelta.getPrices();
						constraint = normDelta.getGeneratedPriceConstraints();
					} else {
						// just update constraints to match new optimization goal (i.e. prices must be
						// clearing prices also with respect to current valuation)
						constraint = new PriceConstraints(domain, setting.getBidders(), currentValuation, allocation,
								constraint);
					}

					done = true;
				} catch (RuntimeException re) {
					localOffset = localOffset.scaleByPowerOfTen(1);
					log.warn("Increasing offset due to infeasibility. New offset: {}", localOffset, re);
				}
			} while (localOffset.compareTo(BigDecimal.ONE) < 0 && !done);
			offset = offset.scaleByPowerOfTen(1);
		}

		try {
			// Maximize prices
			LinearPriceMaximizePricesMIP max = new LinearPriceMaximizePricesMIP(domain, setting.getBidders(),
					allocation, constraint, timeLimit);
			prices = max.getPrices();

			try {
				// Make prices unique
				LinearPriceMinimizePricesNormMIP euclid = new LinearPriceMinimizePricesNormMIP(domain,
						setting.getBidders(), allocation, constraint, max.getPriceSum(), timeLimit);
				prices = euclid.getPrices();
			} catch (RuntimeException e) {
				log.warn("Unable to minimize euclidean distance of prices");
			}
		} catch (RuntimeException e) {
			log.warn("Unable to maximize prices");
		}

		return prices.divide(scalingFactor);
	}
}
