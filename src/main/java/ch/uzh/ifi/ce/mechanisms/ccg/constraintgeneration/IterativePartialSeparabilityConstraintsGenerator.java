package ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration;

import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation.BlockedBidders;
import ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation.BlockedBiddersBuilder;
import ch.uzh.ifi.ce.mechanisms.ccg.paymentrules.CorePaymentRule;
import ch.uzh.ifi.ce.domain.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class IterativePartialSeparabilityConstraintsGenerator implements PartialConstraintGenerator {

    @Override
    public void generateFirstRoundConstraints(AuctionInstance auctionInstance, AuctionResult referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule) {

    }

    @Override
    public void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
                                   ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, AuctionResult lastResult) {
        NeighborCache<PotentialCoalition, DefaultEdge> neighborIndex = new NeighborCache<>(graph);
        Allocation winningAllocation = lastResult.getAllocation();
        for (Set<PotentialCoalition> subGraph : connectivityInspector.connectedSets()) {
            Set<Set<PotentialCoalition>> allreadyAddedCoalitions = new HashSet<>();
            Set<PotentialCoalition> subGraphWinners = ImmutableSet.copyOf(Sets.intersection(subGraph, winningAllocation.getPotentialCoalitions()));
            for (PotentialCoalition winner : subGraphWinners) {
                Set<PotentialCoalition> blockedBidderSet = new HashSet<>(subGraphWinners.size());
                blockedBidderSet.add(winner);
                BlockedBidders nStepBlockingBidders = BlockedBidders.emptyBlockedBidders();
                while (blockedBidderSet.size() < subGraphWinners.size() && !allreadyAddedCoalitions.contains(nStepBlockingBidders.getBlockingBidders())) {
                    if (nStepBlockingBidders != BlockedBidders.emptyBlockedBidders()) {
                        corePaymentRule.addBlockingConstraint(nStepBlockingBidders, lastResult.getPayment());
                        allreadyAddedCoalitions.add(nStepBlockingBidders.getBlockingBidders());
                    }
                    nStepBlockingBidders = findNStepBlockingBidders(blockedBidderSet, neighborIndex, blockingCoalition);

                }
            }
        }
    }

    private BlockedBidders findNStepBlockingBidders(Set<PotentialCoalition> startingPoints, NeighborCache<PotentialCoalition, DefaultEdge> index, Allocation blockingCoalition) {
        Set<PotentialCoalition> blockingBidders = new HashSet<>();
        BlockedBiddersBuilder builder = new BlockedBiddersBuilder();
        for (PotentialCoalition startingPoint : startingPoints) {
            for (PotentialCoalition blockingBidder : index.neighborsOf(startingPoint)) {
                builder.addBlockingBidder(blockingBidder);
                blockingBidders.add(blockingBidder);
            }
        }
        for (PotentialCoalition blockingBidder : blockingBidders) {
            for (PotentialCoalition blockedBidder : index.neighborsOf(blockingBidder)) {
                builder.addBlockedBidder(blockedBidder.getBidder());
                startingPoints.add(blockedBidder);
            }
        }

        return builder.build();
    }
}
