package org.marketdesignresearch.mechlib.outcomerules.ccg;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;

public interface MechanismFactory {

    OutcomeRule getOutcomeRule(Bids bids);

    String getOutcomeRuleName();
}
