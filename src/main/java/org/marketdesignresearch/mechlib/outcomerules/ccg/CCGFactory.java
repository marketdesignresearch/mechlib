package org.marketdesignresearch.mechlib.outcomerules.ccg;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.Outcome;

public interface CCGFactory extends MechanismFactory {
    @Override
    CCGOutcomeRule getOutcomeRule(Bids bids);

    void setReferencePoint(Outcome cachedReferencePoint);

}
