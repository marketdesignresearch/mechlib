package org.marketdesignresearch.mechlib.mechanisms.ccg;

import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;

public interface CCGMechanismFactory extends MechanismFactory {
    @Override
    CCGAuction getMechanism(AuctionInstance auctionInstance);

    void setReferencePoint(AuctionResult cachedReferencePoint);

}
