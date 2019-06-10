package org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.graph.DefaultEdge;
import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.BlockedBiddersBuilder;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.CorePaymentRule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class PartialSeparabilityConstraintGenerator implements PartialConstraintGenerator {

    @Override
    public void generateFirstRoundConstraints(Bids bids, MechanismResult referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule) {

    }

    @Override
    public void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
                                   ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, MechanismResult lastResult) {
        NeighborCache<PotentialCoalition, DefaultEdge> neighborIndex = new NeighborCache<>(graph);

        Allocation winningAllocation = lastResult.getAllocation();
        for (PotentialCoalition potentialCoalition : winningAllocation.getPotentialCoalitions()) {

            Set<PotentialCoalition> blockingNeighbors = neighborIndex.neighborsOf(potentialCoalition);
            Set<Bidder> blockedBidders = new HashSet<>(winningAllocation.getWinners().size());
            for (PotentialCoalition neighbor : blockingNeighbors) {
                blockedBidders.addAll(neighborIndex.neighborsOf(neighbor).stream().map(PotentialCoalition::getBidder).collect(Collectors.toList()));
            }
            BlockedBiddersBuilder blockedBiddersBuilder = new BlockedBiddersBuilder();
            blockingNeighbors.forEach(blockedBiddersBuilder::addBlockingBidder);
            if (blockedBidders.size() < winningAllocation.getWinners().size()) {
                corePaymentRule.addBlockingConstraint(blockedBiddersBuilder.build(), lastResult.getPayment());
            }

        }
    }
}
