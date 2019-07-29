package org.marketdesignresearch.mechlib.mechanisms.ccg;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.OutputRule;

public interface MechanismFactory {

    OutputRule getMechanism(Bids bids);

    String getMechanismName();
}
