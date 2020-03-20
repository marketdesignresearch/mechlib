package org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration;

import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.graph.DefaultEdge;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockedBiddersBuilder;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.CorePaymentRule;

import com.google.common.collect.Sets;

/**
 * Generates all possibly interesting constraints.</br> This is exponential in
 * the number of winners and is thus only interesting for theoretical purposes
 * 
 * @author Benedikt
 *
 */
public class FullConstraintGenerator implements PartialConstraintGenerator {

    @Override
    public void generateFirstRoundConstraints(BundleValueBids<?> bids, Outcome referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule) {

    }

    @Override
    public void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
                                   ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, Outcome priorResult) {
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
