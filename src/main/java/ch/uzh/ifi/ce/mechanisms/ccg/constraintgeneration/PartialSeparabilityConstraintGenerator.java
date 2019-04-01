package ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration;

import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation.BlockedBiddersBuilder;
import ch.uzh.ifi.ce.mechanisms.ccg.paymentrules.CorePaymentRule;
import ch.uzh.ifi.ce.domain.*;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class PartialSeparabilityConstraintGenerator implements PartialConstraintGenerator {

    @Override
    public void generateFirstRoundConstraints(AuctionInstance auctionInstance, AuctionResult referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule) {

    }

    @Override
    public void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
                                   ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, AuctionResult lastResult) {
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
