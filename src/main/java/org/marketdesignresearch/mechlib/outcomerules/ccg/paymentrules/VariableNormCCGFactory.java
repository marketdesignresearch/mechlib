package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import java.util.List;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.outcomerules.ccg.ConfigurableCCGFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.XORBlockingCoalitionFinderFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.ReferencePointFactory;

import com.google.common.collect.Lists;

public class VariableNormCCGFactory extends ConfigurableCCGFactory {

	public VariableNormCCGFactory(ReferencePointFactory rpFactory, NormFactory primaryNormFactory,
			NormFactory... secondaryNormFactories) {
		this(rpFactory, Lists.asList(primaryNormFactory, secondaryNormFactories));

	}

	public VariableNormCCGFactory(ReferencePointFactory rpFactory, List<NormFactory> normFactories) {
		this(rpFactory, normFactories, ConstraintGenerationAlgorithm.SEPARABILITY);
	}

	public VariableNormCCGFactory(ReferencePointFactory rpFactory, List<NormFactory> normFactories,
			ConstraintGenerationAlgorithm alg) {
		super(new XORBlockingCoalitionFinderFactory(), rpFactory, normFactories, alg);
	}

	public VariableNormCCGFactory(ReferencePointFactory rpFacory, Norm primaryNorm, Norm... secondaryNorms) {
		this(rpFacory, Lists.asList(primaryNorm, secondaryNorms).stream().map(NormFactory::withEqualWeights)
				.collect(Collectors.toList()));

	}
}
