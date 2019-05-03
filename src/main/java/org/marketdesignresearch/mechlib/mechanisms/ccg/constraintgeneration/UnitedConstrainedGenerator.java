package org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.CorePaymentRule;

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
            // TODO: Using keySet assumes that there are only single-availability goods
            for (Good good : coalition.getBundle().keySet()) {
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
            // TODO: Using keySet assumes that there are only single-availability goods
            for (Good good : coalition.getBundle().keySet()) {
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