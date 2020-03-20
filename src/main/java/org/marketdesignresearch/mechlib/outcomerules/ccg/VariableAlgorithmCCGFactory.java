package org.marketdesignresearch.mechlib.outcomerules.ccg;

import java.util.Set;

import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockingAllocationFinder;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.Norm;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.NormFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.ReferencePointFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.VCGReferencePointFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

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
