package org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration;

import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.CorePaymentRule;

/**
 * <p>
 * Defines a constraint generator that may add valid constraints to a BPO in a
 * preliminary round or in every iteration of the ccg algorithm.
 * </p>
 * The constraints do not need to define a full core. The interface and the
 * classes are therefore package private as not all combinations can be used
 * 
 * @author Benedikt
 * 
 */
interface PartialConstraintGenerator {
    /**
     * 
     * @param corePaymentRule
     *            Needs to be reset if a constraint is added
     * @param goodToBidderMap
     * @param bids
     */
    void generateFirstRoundConstraints(BundleValueBids<?> bids, Outcome referencePoint, Map<Good, Set<PotentialCoalition>> goodToBidderMap, CorePaymentRule corePaymentRule);

    void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
                            ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, Outcome priorResult);
}
