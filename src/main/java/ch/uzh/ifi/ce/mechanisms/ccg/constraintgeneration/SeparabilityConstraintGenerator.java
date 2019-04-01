package ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration;

import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation.BlockedBidders;
import ch.uzh.ifi.ce.mechanisms.ccg.paymentrules.CorePaymentRule;
import ch.uzh.ifi.ce.domain.*;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;

import java.util.Map;
import java.util.Set;

/**
 * This {@link PartialConstraintGenerator} generates a full constraint set. It
 * will add a single constraint for each independent subgraph of the coalition
 * conflict graph These constraints are at least as strong as the added
 * constraints in the original CCG algorithm
 * 
 * @author Benedikt
 * 
 */
public class SeparabilityConstraintGenerator implements PartialConstraintGenerator {
    @Override
    public void generateFirstRoundConstraints(AuctionInstance auctionInstance, AuctionResult referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule) {

    }

    @Override
    public void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
                                   ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, AuctionResult lastResult) {

        // For each independent sub graph
        for (Set<PotentialCoalition> subGraph : connectivityInspector.connectedSets()) {

            BlockedBidders blockedBidders = BlockedBidders.from(subGraph, blockingCoalition.getPotentialCoalitions());
            corePaymentRule.addBlockingConstraint(blockedBidders, lastResult.getPayment());

        }
    }

}
