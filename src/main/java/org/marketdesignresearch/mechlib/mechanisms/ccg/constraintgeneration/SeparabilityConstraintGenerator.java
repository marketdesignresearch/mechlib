package org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.BlockedBidders;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.CorePaymentRule;

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
    public void generateFirstRoundConstraints(Bids bids, MechanismResult referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule) {

    }

    @Override
    public void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
                                   ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, MechanismResult lastResult) {

        // For each independent sub graph
        for (Set<PotentialCoalition> subGraph : connectivityInspector.connectedSets()) {

            BlockedBidders blockedBidders = BlockedBidders.from(subGraph, blockingCoalition.getPotentialCoalitions());
            corePaymentRule.addBlockingConstraint(blockedBidders, lastResult.getPayment());

        }
    }

}
