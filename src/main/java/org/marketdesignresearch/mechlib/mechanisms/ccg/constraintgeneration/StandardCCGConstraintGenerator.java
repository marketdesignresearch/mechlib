package org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.BlockedBidders;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.CorePaymentRule;

import java.math.BigDecimal;
import java.util.Map;

public class StandardCCGConstraintGenerator implements PartialConstraintGenerator {

    @Override
    public void generateFirstRoundConstraints(Bids bids, AuctionResult referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule) {

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
                                   ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, AuctionResult lastResult) {
        BigDecimal coalitionalValue = blockingCoalition.getTotalAllocationValue();
        BlockedBidders blockedBidders = new BlockedBidders(lastResult.getWinners(), blockingCoalition.getPotentialCoalitions(), coalitionalValue);
        corePaymentRule.addBlockingConstraint(blockedBidders, lastResult.getPayment());

    }

}
