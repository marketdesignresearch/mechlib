package org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.graph.DefaultEdge;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockedBidders;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockedBiddersBuilder;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.CorePaymentRule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class IterativePartialSeparabilityConstraintsGenerator implements PartialConstraintGenerator {

    @Override
    public <T extends BundleValuePair> void generateFirstRoundConstraints(BundleValueBids<T> bids, Outcome referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule) {

    }

    @Override
    public void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
                                   ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, Outcome lastResult) {
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
