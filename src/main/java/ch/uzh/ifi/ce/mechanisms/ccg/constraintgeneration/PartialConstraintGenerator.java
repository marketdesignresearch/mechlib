package ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration;

import ch.uzh.ifi.ce.mechanisms.ccg.paymentrules.CorePaymentRule;
import ch.uzh.ifi.ce.domain.*;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;

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
     * @param auctionInstance
     */
    void generateFirstRoundConstraints(AuctionInstance auctionInstance, AuctionResult referencePoint, Map<Good, PotentialCoalition> goodToBidderMap, CorePaymentRule corePaymentRule);

    void generateConstraint(CorePaymentRule corePaymentRule, Graph<PotentialCoalition, DefaultEdge> graph,
                            ConnectivityInspector<PotentialCoalition, DefaultEdge> connectivityInspector, Allocation blockingCoalition, AuctionResult priorResult);
}
