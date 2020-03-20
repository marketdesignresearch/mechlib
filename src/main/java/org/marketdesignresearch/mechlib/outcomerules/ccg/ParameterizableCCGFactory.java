package org.marketdesignresearch.mechlib.outcomerules.ccg;

import java.math.BigDecimal;

import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.BlockingAllocationFinder;

public interface ParameterizableCCGFactory extends MechanismFactory {

    BlockingAllocationFinder getBlockingAllocationFinder();

    String lubinParkesName();

    boolean enforceMRC();

    BigDecimal getDelta();

    BigDecimal getEpsilon();

    String tieBreaker();

    String getReferencePoint();

    boolean referencePointBelowCore();

    @Override
    default String getOutcomeRuleName() {
        String mrc = enforceMRC() ? "MRC" : "";
        return getEpsilon() + "e" + getDelta() + "d" + mrc + lubinParkesName() + "tiebrk" + tieBreaker() + getReferencePoint();
    }

}