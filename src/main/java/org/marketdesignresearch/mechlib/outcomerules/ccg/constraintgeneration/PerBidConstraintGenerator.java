package org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockedBidders;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.CorePaymentRule;

import com.google.common.collect.Sets;

/**
 * This class should not be instantiated alone as it will not generate all core
 * constraints
 *
 * @author Benedikt
 */
class PerBidConstraintGenerator implements PartialConstraintGenerator {
	@Override
	public void generateFirstRoundConstraints(BundleValueBids<?> bids, Outcome referencePoint,
			Map<Good, Set<PotentialCoalition>> goodToBidderMap, CorePaymentRule corePaymentRule) {
		corePaymentRule.resetResult();
		Allocation allocation = referencePoint.getAllocation();
		Payment lowerBound = referencePoint.getPayment();
		for (Entry<Bidder, ? extends BundleValueBid<?>> bid : bids.getBidMap().entrySet()) {
			for (BundleExactValuePair bundleBid : bid.getValue().getBundleBids()) {
				// TODO check implementation for availability of more than 1

				Set<Bidder> blockedBiddersSet = bundleBid.getBundle().getBundleEntries().stream()
						.map(BundleEntry::getGood).filter(goodToBidderMap::containsKey)
						.flatMap(good -> goodToBidderMap.get(good).stream().map(PotentialCoalition::getBidder))
						.collect(Collectors.toCollection(LinkedHashSet::new));
				BigDecimal currentValue = allocation.allocationOf(bid.getKey()) == null ? BigDecimal.ZERO
						: allocation.allocationOf(bid.getKey()).getValue();
				BigDecimal currentPayment = lowerBound.paymentOf(bid.getKey()) == null ? BigDecimal.ZERO
						: lowerBound.paymentOf(bid.getKey()).getAmount();
				BigDecimal blockingValue = bundleBid.getAmount().subtract(currentValue).add(currentPayment);
				if (blockingValue.signum() > 0) {
					BlockedBidders blockedBidders = new BlockedBidders(blockedBiddersSet,
							Sets.newHashSet(bundleBid.getPotentialCoalition(bid.getKey())), blockingValue);
					corePaymentRule.addBlockingConstraint(blockedBidders, lowerBound);
				}
			}
		}
	}

	@Override
	public void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
			ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition,
			Outcome priorResult) {

	}

}
