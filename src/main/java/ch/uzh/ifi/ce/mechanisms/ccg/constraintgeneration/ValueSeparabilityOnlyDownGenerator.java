package ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration;

import ch.uzh.ifi.ce.mechanisms.ccg.paymentrules.CorePaymentRule;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation.BlockedBidders;
import ch.uzh.ifi.ce.domain.Payment;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.graph.DefaultEdge;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class ValueSeparabilityOnlyDownGenerator extends ValueSeparabilityGenerator implements PartialConstraintGenerator {
    @Override
    protected void investigateSubCoalition(Set<PotentialCoalition> blockingBidders, NeighborCache<PotentialCoalition, DefaultEdge> neighbors, Bidder exludedBidder,
                                           AverageDistanceFromReference minadr, Payment priorPayment, CorePaymentRule corePaymentRule) {
        Graph<PotentialCoalition, DefaultEdge> newGraph = createConflictGraph(blockingBidders, neighbors, exludedBidder);
        ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector = new ConnectivityInspector<>(newGraph);
        Stream<BlockedBidders> blockedBidders = connectivityInspector.connectedSets().stream().map(subGraph -> BlockedBidders.from(subGraph, blockingBidders));
        Set<Bidder> minAdrBidders = minadr.getBlockedBidders();
        Optional<SimpleImmutableEntry<BlockedBidders, AverageDistanceFromReference>> maxEntry = blockedBidders.map(
                bb -> new SimpleImmutableEntry<>(bb, calcAdr(bb, priorPayment, minAdrBidders))).max(Comparator.comparing(Entry::getValue));
        if (maxEntry.isPresent()) {
            AverageDistanceFromReference adr = maxEntry.get().getValue();
            if (adr.compareTo(minadr) > 0 && adr.getAverageDistance().signum() > 0) {
                addConstraints(maxEntry.get().getKey(), neighbors, priorPayment, corePaymentRule, adr);
            }
        }
    }
}
