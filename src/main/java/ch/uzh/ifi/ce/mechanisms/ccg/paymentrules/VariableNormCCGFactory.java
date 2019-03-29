package ch.uzh.ifi.ce.mechanisms.ccg.paymentrules;

import ch.uzh.ifi.ce.mechanisms.ccg.ConfigurableCCGFactory;
import ch.uzh.ifi.ce.mechanisms.ccg.referencepoint.ReferencePointFactory;
import ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation.XORBlockingCoalitionFinderFactory;
import ch.uzh.ifi.ce.mechanisms.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import ch.uzh.ifi.ce.mechanisms.ccg.MechanismFactory;
import com.google.common.collect.Lists;

import java.util.List;

public class VariableNormCCGFactory extends ConfigurableCCGFactory implements MechanismFactory {

    /**
     * 
     */

    public VariableNormCCGFactory(ReferencePointFactory rpFactory, NormFactory primaryNormFactory, NormFactory... secondaryNormFactories) {
        this(rpFactory, Lists.asList(primaryNormFactory, secondaryNormFactories));

    }

    public VariableNormCCGFactory(ReferencePointFactory rpFactory, List<NormFactory> normFactories) {
        super(new XORBlockingCoalitionFinderFactory(), rpFactory, normFactories, ConstraintGenerationAlgorithm.SEPARABILITY);
    }

    public VariableNormCCGFactory(ReferencePointFactory rpFacory, Norm primaryNorm, Norm... secondaryNorms) {
        this(rpFacory, Lists.transform(Lists.asList(primaryNorm, secondaryNorms), NormFactory::withEqualWeights));

    }
}
