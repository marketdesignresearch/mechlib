package org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration;

import com.google.common.collect.Sets;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.graph.DefaultEdge;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.BlockedBiddersBuilder;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.CorePaymentRule;

import java.util.Map;
import java.util.Set;

/**
 * Generates all possibly interesting constraints.</br> This is exponential in
 * the number of winners and is thus only interesting for theoretical purposes
 * 
 * @author Benedikt
 *
 */
public class FullConstraintGenerator implements PartialConstraintGenerator {

    @Override
    public void generateFirstRoundConstraints(Bids bids, MechanismResult referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule) {

    }

    @Override
    public void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
                                   ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, MechanismResult priorResult) {
        NeighborCache<PotentialCoalition, DefaultEdge> neighborIndex = new NeighborCache<>(graph);
        for (Set<PotentialCoalition> subgraph : connectivityInspector.connectedSets()) {
            Set<PotentialCoalition> winners = Sets.intersection(subgraph, priorResult.getAllocation().getPotentialCoalitions());
            Set<Set<PotentialCoalition>> powerSet = Sets.powerSet(winners);
            for (Set<PotentialCoalition> winnerSubSet : powerSet) {
                BlockedBiddersBuilder blockedBidderBuilder = new BlockedBiddersBuilder();
                for (PotentialCoalition winner : winnerSubSet) {
                    blockedBidderBuilder.addBlockedBidder(winner.getBidder());
                }
                Sets.intersection(subgraph, blockingCoalition.getPotentialCoalitions()).stream().filter(pc -> winnerSubSet.containsAll(neighborIndex.neighborsOf(pc)))
                        .forEach(blockedBidderBuilder::addBlockingBidder);
                corePaymentRule.addBlockingConstraint(blockedBidderBuilder.build(), priorResult.getPayment());
            }
        }
    }
}
