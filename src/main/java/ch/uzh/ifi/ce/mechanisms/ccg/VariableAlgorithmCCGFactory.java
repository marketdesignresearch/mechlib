package ch.uzh.ifi.ce.mechanisms.ccg;

import ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation.BlockingAllocationFinder;
import ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import ch.uzh.ifi.ce.mechanisms.ccg.paymentrules.Norm;
import ch.uzh.ifi.ce.mechanisms.ccg.paymentrules.NormFactory;
import ch.uzh.ifi.ce.mechanisms.ccg.referencepoint.ReferencePointFactory;
import ch.uzh.ifi.ce.mechanisms.ccg.referencepoint.VCGReferencePointFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import java.util.Set;

public class VariableAlgorithmCCGFactory extends ConfigurableCCGFactory implements MechanismFactory {
    /**
     * 
     */

    public VariableAlgorithmCCGFactory(ReferencePointFactory rpFactory, BlockingAllocationFinder blockingAllocationFinder, Set<ConstraintGenerationAlgorithm> algorithms) {
        super(blockingAllocationFinder, rpFactory, ImmutableList.of(NormFactory.withEqualWeights(Norm.MANHATTAN), NormFactory.withEqualWeights(Norm.EUCLIDEAN)), algorithms);
    }

    public VariableAlgorithmCCGFactory(BlockingAllocationFinder blockingAllocationFinder, ConstraintGenerationAlgorithm algorithm, ConstraintGenerationAlgorithm... algorithms) {
        this(new VCGReferencePointFactory(), blockingAllocationFinder, Sets.immutableEnumSet(algorithm, algorithms));
    }

}
