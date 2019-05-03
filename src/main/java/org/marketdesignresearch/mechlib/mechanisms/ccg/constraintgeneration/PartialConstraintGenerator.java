package org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.Bids;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.CorePaymentRule;

import java.util.Map;

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
    void generateFirstRoundConstraints(Bids bids, AuctionResult referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule);

    void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
                            ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, AuctionResult priorResult);
}
