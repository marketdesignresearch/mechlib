package org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration;

import com.google.common.collect.Sets;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules.CorePaymentRule;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum ConstraintGenerationAlgorithm {
    /** CCG */
    STANDARD_CCG,
    /** S */
    SEPARABILITY,
    /** PS */
    PARTIAL_SEPARABILITY,
    /** IPS */
    ITERATIVE_PARTIAL_SEPARABILITY,
    /** PBC */
    PER_BID_CONSTRAINTS,
    /** VS */
    VALUE_SEPARABILITY,
    /** VS DOWN */
    VALUE_SEPARABILITY_DOWN, FULL;
    public static ConstraintGenerator getInstance(Bids bids, MechanismResult referencePoint, CorePaymentRule corePaymentRule, ConstraintGenerationAlgorithm... algorithms) {
        Set<ConstraintGenerationAlgorithm> algorithmSet = Sets.newHashSet(algorithms);
        return getInstance(algorithmSet, bids, referencePoint, corePaymentRule);

    }

    public static ConstraintGenerator getInstance(Set<ConstraintGenerationAlgorithm> algorithms, Bids bids, MechanismResult referencePoint, CorePaymentRule corePaymentRule) {
        Set<PartialConstraintGenerator> constraintGenerators = new HashSet<>(algorithms.size());
        constraintGenerators.addAll(algorithms.stream().map(ConstraintGenerationAlgorithm::getConstraintGenerator).collect(Collectors.toList()));
        return new UnitedConstrainedGenerator(bids, referencePoint, constraintGenerators, corePaymentRule);
    }

    private PartialConstraintGenerator getConstraintGenerator() {
        switch (this) {
            case ITERATIVE_PARTIAL_SEPARABILITY:
                return new IterativePartialSeparabilityConstraintsGenerator();
            case PARTIAL_SEPARABILITY:
                return new PartialSeparabilityConstraintGenerator();
            case PER_BID_CONSTRAINTS:
                return new PerBidConstraintGenerator();
            case SEPARABILITY:
                return new SeparabilityConstraintGenerator();
            case STANDARD_CCG:
                return new StandardCCGConstraintGenerator();
            case VALUE_SEPARABILITY:
                return new ValueSeparabilitySideDownGenerator();
            case VALUE_SEPARABILITY_DOWN:
                return new ValueSeparabilityOnlyDownGenerator();
            case FULL:
                return new FullConstraintGenerator();
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + toString());
        }

    }

}
