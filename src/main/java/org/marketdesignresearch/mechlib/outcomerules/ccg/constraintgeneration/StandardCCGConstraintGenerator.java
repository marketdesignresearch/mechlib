package org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockedBidders;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.CorePaymentRule;

import java.math.BigDecimal;
import java.util.Map;

public class StandardCCGConstraintGenerator implements PartialConstraintGenerator {

    @Override
    public <T extends BundleValuePair> void generateFirstRoundConstraints(BundleValueBids<T> bids, Outcome referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule) {

    }

    /**
     *   
     * See Day, Raghavan 2007 for detailed explanation. The Winners need to
     * offer the auctioneer a higher value than the blocking coalition can
     * offer. Any increase in the payments of W /\ C will increase both the
     * coalition's value as well as the winners payment linearly. The
     * payments of W/\C can thus be treated as constant. The non-colluding
     * bidders therefore need to increase their payments until it surpasses
     * the coalition value minus the payment of the traitors at point t.
     * W\C >= v_t(C)-p_t(W/\C)
     */
    @Override
    public void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
                                   ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, Outcome lastResult) {
        BigDecimal coalitionalValue = blockingCoalition.getTotalAllocationValue();
        BlockedBidders blockedBidders = new BlockedBidders(lastResult.getWinners(), blockingCoalition.getPotentialCoalitions(), coalitionalValue);
        corePaymentRule.addBlockingConstraint(blockedBidders, lastResult.getPayment());

    }

}
