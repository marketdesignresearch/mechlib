package org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BidderPayment;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockedBidders;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.CorePaymentRule;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public abstract class ValueSeparabilityGenerator implements PartialConstraintGenerator {

    private Payment referencePayments = null;

    protected abstract void investigateSubCoalition(Set<PotentialCoalition> blockingBidders, NeighborCache<PotentialCoalition, DefaultEdge> neighbors, Bidder exludedBidder,
                                                    AverageDistanceFromReference minadr, Payment priorPayment, CorePaymentRule corePaymentRule);

    private final AtomicInteger constraintCount = new AtomicInteger();
    private static final int MAX_CONSTRAINTS = 2000;

    public ValueSeparabilityGenerator() {
        super();
    }

    @Override
    public void generateFirstRoundConstraints(BundleValueBids<?> bids, Outcome referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule) {
        this.referencePayments = referencePoint.getPayment();
    }

    @Override
    public void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
                                   ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, Outcome priorResult) {
        constraintCount.set(0);
        NeighborCache<PotentialCoalition, DefaultEdge> neighbors = new NeighborCache<>(graph);
        List<ForkJoinTask<?>> graphInspecting = new LinkedList<>();
        for (Set<PotentialCoalition> subgraph : connectivityInspector.connectedSets()) {
            ForkJoinTask<?> task = ForkJoinTask.adapt(() -> {
                BlockedBidders blockedBidders = BlockedBidders.from(subgraph, blockingCoalition.getPotentialCoalitions());
                AverageDistanceFromReference distancePerBidder = calcAdr(blockedBidders, priorResult.getPayment(), blockedBidders.getNonTraitors());

                addConstraints(blockedBidders, neighbors, priorResult.getPayment(), corePaymentRule, distancePerBidder);
            });
            graphInspecting.add(task.fork());
        }
        graphInspecting.forEach(ForkJoinTask::join);
    }

    protected void addConstraints(BlockedBidders blockedBidders, NeighborCache<PotentialCoalition, DefaultEdge> neighbors, Payment priorPayment, CorePaymentRule corePaymentRule,
                                  AverageDistanceFromReference minadr) {
        if (!blockedBidders.getNonTraitors().isEmpty()) {
            corePaymentRule.addBlockingConstraint(blockedBidders, priorPayment);

            if (constraintCount.getAndIncrement() < MAX_CONSTRAINTS) {
                Consumer<Bidder> subCoalitionInvestigator = b -> investigateSubCoalition(blockedBidders.getBlockingBidders(), neighbors, b, minadr, priorPayment, corePaymentRule);
                blockedBidders.getBlockedBidders().parallelStream().forEach(subCoalitionInvestigator);
            }
        }

    }

    protected Graph<PotentialCoalition, DefaultEdge> createConflictGraph(Set<PotentialCoalition> blockingBidders,
                                                                                   NeighborCache<PotentialCoalition, DefaultEdge> neighbors, Bidder exludedBidder) {
        Graph<PotentialCoalition, DefaultEdge> newGraph = new SimpleGraph<>(DefaultEdge.class);
        for (PotentialCoalition blockingBidder : blockingBidders) {

            newGraph.addVertex(blockingBidder);
            for (PotentialCoalition blockedBidder : neighbors.neighborsOf(blockingBidder)) {
                if (Objects.equals(blockedBidder.getBidder(), exludedBidder)) {
                    newGraph.removeVertex(blockingBidder);
                    break;
                }
                newGraph.addVertex(blockedBidder);
                newGraph.addEdge(blockingBidder, blockedBidder);

            }

        }
        return newGraph;
    }

    protected AverageDistanceFromReference calcAdr(BlockedBidders blockedBidders, Payment priorPayment, Set<? extends Bidder> superAllocationBlockedBidders) {
        BigDecimal constraintValue = blockedBidders.getBlockedAmount(priorPayment);
        BigDecimal totalReferencePayments = blockedBidders.getNonTraitors().stream().map(referencePayments::paymentOf).map(BidderPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDistance = constraintValue.subtract(totalReferencePayments);

        return new AverageDistanceFromReference(totalDistance, ImmutableSet.copyOf(Sets.intersection(blockedBidders.getNonTraitors(), superAllocationBlockedBidders)));
    }

}