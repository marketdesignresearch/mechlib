package ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration;

import ch.uzh.ifi.ce.mechanisms.ccg.paymentrules.CorePaymentRule;
import ch.uzh.ifi.ce.domain.*;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UnitedConstrainedGenerator implements ConstraintGenerator {
    private final Set<PartialConstraintGenerator> generatorAlgorithms;
    private final Map<Good, PotentialCoalition> goodToCoalitionMap = new HashMap<>();
    private final CorePaymentRule corePaymentRule;

    public UnitedConstrainedGenerator(AuctionInstance auctionInstance, AuctionResult referencePoint, Set<PartialConstraintGenerator> generatorAlgorithms, CorePaymentRule corePaymentRule) {
        this.generatorAlgorithms = generatorAlgorithms;
        this.corePaymentRule = corePaymentRule;

        for (PotentialCoalition coalition : referencePoint.getAllocation().getPotentialCoalitions()) {
            for (Good good : coalition.getGoods()) {
                goodToCoalitionMap.put(good, coalition);
            }
        }
        for (PartialConstraintGenerator particalConstraintGenerator : generatorAlgorithms) {
            particalConstraintGenerator.generateFirstRoundConstraints(auctionInstance, referencePoint, goodToCoalitionMap, corePaymentRule);
        }

    }

    @Override
    public void addConstraint(Allocation blockingCoalition, AuctionResult priorResult) {

        corePaymentRule.resetResult();
        Graph<PotentialCoalition, DefaultEdge> tempGraph = new SimpleGraph<>(DefaultEdge.class);
        priorResult.getAllocation().getPotentialCoalitions().forEach(tempGraph::addVertex);
        for (PotentialCoalition coalition : blockingCoalition.getPotentialCoalitions()) {
            tempGraph.addVertex(coalition);
            for (Good good : coalition.getGoods()) {
                PotentialCoalition blockedCoalition = goodToCoalitionMap.get(good);

                if (blockedCoalition != null) {
                    tempGraph.addEdge(coalition, blockedCoalition);
                }
            }

        }
        ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector = new ConnectivityInspector<>(tempGraph);
        for (PartialConstraintGenerator cg : generatorAlgorithms) {
            cg.generateConstraint(corePaymentRule, tempGraph, connectivityInspector, blockingCoalition, priorResult);
        }

    }

    @Override
    public String toString() {
        return "UnitedConstraintGenerator[algorithms=" + generatorAlgorithms + "]";
    }

}