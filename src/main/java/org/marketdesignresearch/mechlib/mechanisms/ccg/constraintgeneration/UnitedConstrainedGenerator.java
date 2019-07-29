package org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.CorePaymentRule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UnitedConstrainedGenerator implements ConstraintGenerator {
    private final Set<PartialConstraintGenerator> generatorAlgorithms;
    private final Map<Good, PotentialCoalition> goodToCoalitionMap = new HashMap<>();
    private final CorePaymentRule corePaymentRule;

    public UnitedConstrainedGenerator(Bids bids, MechanismResult referencePoint, Set<PartialConstraintGenerator> generatorAlgorithms, CorePaymentRule corePaymentRule) {
        this.generatorAlgorithms = generatorAlgorithms;
        this.corePaymentRule = corePaymentRule;

        for (PotentialCoalition coalition : referencePoint.getAllocation().getPotentialCoalitions()) {
            // TODO: assumes that there are only single-availability goods
            for (Good good : coalition.getBundle().getBundleEntries().stream().map(BundleEntry::getGood).collect(Collectors.toSet())) {
                goodToCoalitionMap.put(good, coalition);
            }
        }
        for (PartialConstraintGenerator particalConstraintGenerator : generatorAlgorithms) {
            particalConstraintGenerator.generateFirstRoundConstraints(bids, referencePoint, goodToCoalitionMap, corePaymentRule);
        }

    }

    @Override
    public void addConstraint(Allocation blockingCoalition, MechanismResult priorResult) {

        corePaymentRule.resetResult();
        Graph<PotentialCoalition, DefaultEdge> tempGraph = new SimpleGraph<>(DefaultEdge.class);
        priorResult.getAllocation().getPotentialCoalitions().forEach(tempGraph::addVertex);
        for (PotentialCoalition coalition : blockingCoalition.getPotentialCoalitions()) {
            tempGraph.addVertex(coalition);
            // TODO: assumes that there are only single-availability goods
            for (Good good : coalition.getBundle().getBundleEntries().stream().map(BundleEntry::getGood).collect(Collectors.toSet())) {
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