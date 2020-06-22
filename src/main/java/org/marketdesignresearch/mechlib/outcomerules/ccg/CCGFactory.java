package org.marketdesignresearch.mechlib.outcomerules.ccg;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;

public interface CCGFactory extends MechanismFactory {
    @Override
    OutcomeRule getOutcomeRule(BundleValueBids<?> bids);

    void setReferencePoint(Outcome cachedReferencePoint);

}
