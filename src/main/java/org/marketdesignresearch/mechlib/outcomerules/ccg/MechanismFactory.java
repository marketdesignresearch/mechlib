package org.marketdesignresearch.mechlib.outcomerules.ccg;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;

public interface MechanismFactory {

	OutcomeRule getOutcomeRule(BundleValueBids<?> bids);

	String getOutcomeRuleName();
}
