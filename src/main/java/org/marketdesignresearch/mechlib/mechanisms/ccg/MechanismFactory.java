package org.marketdesignresearch.mechlib.mechanisms.ccg;

import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.mechanisms.AuctionMechanism;

public interface MechanismFactory {

    AuctionMechanism getMechanism(AuctionInstance auctionInstance);

    String getMechanismName();
}
