package ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration;

import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation.BlockedBidders;
import ch.uzh.ifi.ce.mechanisms.ccg.paymentrules.CorePaymentRule;
import ch.uzh.ifi.ce.domain.*;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;

import java.math.BigDecimal;
import java.util.Map;

public class StandardCCGConstraintGenerator implements PartialConstraintGenerator {

    @Override
    public void generateFirstRoundConstraints(AuctionInstance auctionInstance, AuctionResult referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule) {

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
