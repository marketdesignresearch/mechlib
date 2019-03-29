package ch.uzh.ifi.ce.mechanisms.ccg;

import ch.uzh.ifi.ce.mechanisms.ccg.blockingallocation.BlockingAllocationFinder;

import java.math.BigDecimal;

public interface ParameterizableMechanismFactory extends MechanismFactory {

    BlockingAllocationFinder getBlockingAllocationFinder();

    String lubinParkesName();

    boolean enforceMRC();

    BigDecimal getDelta();

    BigDecimal getEpsilon();

    String tieBreaker();

    String getReferencePoint();

    boolean referencePointBelowCore();

    @Override
    default String getMechanismName() {
        String mrc = enforceMRC() ? "MRC" : "";
        return getEpsilon() + "e" + getDelta() + "d" + mrc + lubinParkesName() + "tiebrk" + tieBreaker() + getReferencePoint();
    }

}