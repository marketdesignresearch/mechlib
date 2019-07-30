package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import org.marketdesignresearch.mechlib.outcomerules.ccg.ConfigurableCCGFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.ReferencePointFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.XORBlockingCoalitionFinderFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import org.marketdesignresearch.mechlib.outcomerules.ccg.MechanismFactory;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

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
        this(rpFacory, Lists.asList(primaryNorm, secondaryNorms).stream().map(NormFactory::withEqualWeights).collect(Collectors.toList()));

    }
}
