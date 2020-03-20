package org.marketdesignresearch.mechlib.outcomerules.ccg;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;

public interface CCGFactory extends MechanismFactory {
    @Override
    CCGOutcomeRule getOutcomeRule(BundleValueBids<?> bids);

    void setReferencePoint(Outcome cachedReferencePoint);

}
