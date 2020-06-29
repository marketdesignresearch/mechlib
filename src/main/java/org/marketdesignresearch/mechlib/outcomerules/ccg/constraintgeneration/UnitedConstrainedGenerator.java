package org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.CorePaymentRule;

public class UnitedConstrainedGenerator implements ConstraintGenerator {
    private final Set<PartialConstraintGenerator> generatorAlgorithms;
    private final Map<Good, Set<PotentialCoalition>> goodToCoalitionMap = new LinkedHashMap<>();
    private final CorePaymentRule corePaymentRule;

    public UnitedConstrainedGenerator(BundleValueBids<?> bids, Outcome referencePoint, Set<PartialConstraintGenerator> generatorAlgorithms, CorePaymentRule corePaymentRule) {
        this.generatorAlgorithms = generatorAlgorithms;
        this.corePaymentRule = corePaymentRule;

        for (PotentialCoalition coalition : referencePoint.getAllocation().getPotentialCoalitions()) {
            // TODO: check implementation for availability of more than 1
            for (Good good : coalition.getBundle().getBundleEntries().stream().map(BundleEntry::getGood).collect(Collectors.toCollection(LinkedHashSet::new))) {
            	goodToCoalitionMap.putIfAbsent(good, new LinkedHashSet<>());
                goodToCoalitionMap.get(good).add(coalition);
            }
        }
        for (PartialConstraintGenerator particalConstraintGenerator : generatorAlgorithms) {
            particalConstraintGenerator.generateFirstRoundConstraints(bids, referencePoint, goodToCoalitionMap, corePaymentRule);
        }

    }

    @Override
    public void addConstraint(Allocation blockingCoalition, Outcome priorResult) {

        corePaymentRule.resetResult();
        Graph<PotentialCoalition, DefaultEdge> tempGraph = new SimpleGraph<>(DefaultEdge.class);
        priorResult.getAllocation().getPotentialCoalitions().forEach(tempGraph::addVertex);
        for (PotentialCoalition coalition : blockingCoalition.getPotentialCoalitions()) {
            tempGraph.addVertex(coalition);
            // TODO: check implementation for availability of more than 1
            for (Good good : coalition.getBundle().getBundleEntries().stream().map(BundleEntry::getGood).collect(Collectors.toCollection(LinkedHashSet::new))) {
            	if(goodToCoalitionMap.containsKey(good)) {
            		for(PotentialCoalition blockedCoalition : goodToCoalitionMap.get(good)) {
            			tempGraph.addEdge(coalition, blockedCoalition);
            		}
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