package org.marketdesignresearch.mechlib.mechanisms.ccg;

import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.Mechanism;

public interface MechanismFactory {

    Mechanism getMechanism(Bids bids);

    String getMechanismName();
}
