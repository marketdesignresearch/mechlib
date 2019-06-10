package org.marketdesignresearch.mechlib.mechanisms.ccg;

import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;

public interface CCGMechanismFactory extends MechanismFactory {
    @Override
    CCGMechanism getMechanism(Bids bids);

    void setReferencePoint(MechanismResult cachedReferencePoint);

}
