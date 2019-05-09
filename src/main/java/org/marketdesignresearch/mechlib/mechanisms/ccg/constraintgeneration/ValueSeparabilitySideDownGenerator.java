package org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration;

import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.CorePaymentRule;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.BlockedBidders;
import org.marketdesignresearch.mechlib.domain.Payment;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.graph.DefaultEdge;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValueSeparabilitySideDownGenerator extends ValueSeparabilityGenerator implements PartialConstraintGenerator {

    @Override
    protected void investigateSubCoalition(Set<PotentialCoalition> blockingBidders, NeighborCache<PotentialCoalition, DefaultEdge> neighbors, Bidder exludedBidder,
                                           AverageDistanceFromReference minadr, Payment priorPayment, CorePaymentRule corePaymentRule) {
        Graph<PotentialCoalition, DefaultEdge> newGraph = createConflictGraph(blockingBidders, neighbors, exludedBidder);
        ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector = new ConnectivityInspector<>(newGraph);
        Stream<BlockedBidders> blockedBidders = connectivityInspector.connectedSets().stream().map(subGraph -> BlockedBidders.from(subGraph, blockingBidders));
        Set<Bidder> minAdrBidders = minadr.getBlockedBidders();
        Map<BlockedBidders, AverageDistanceFromReference> adrMap = blockedBidders.collect(Collectors.toMap(Function.identity(), bb -> calcAdr(bb, priorPayment, minAdrBidders)));
        while (!adrMap.isEmpty()) {

            Entry<BlockedBidders, AverageDistanceFromReference> maxEntry = Collections.max(adrMap.entrySet(), Comparator.comparing(Entry::getValue));
            AverageDistanceFromReference adr = maxEntry.getValue();
            if (adr.compareTo(minadr) > 0 && adr.getAverageDistance().signum() > 0) {
                addConstraints(maxEntry.getKey(), neighbors, priorPayment, corePaymentRule, adr);
                minadr = minadr.subtract(adr);
                adrMap.remove(maxEntry.getKey());
            } else {
                break;
            }
        }
    }

}
