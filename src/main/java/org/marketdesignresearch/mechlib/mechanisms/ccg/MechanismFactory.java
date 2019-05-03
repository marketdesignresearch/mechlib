package org.marketdesignresearch.mechlib.mechanisms.ccg;

import org.marketdesignresearch.mechlib.domain.Bids;
import org.marketdesignresearch.mechlib.mechanisms.AuctionMechanism;

public interface MechanismFactory {

    AuctionMechanism getMechanism(Bids bids);

    String getMechanismName();
}
