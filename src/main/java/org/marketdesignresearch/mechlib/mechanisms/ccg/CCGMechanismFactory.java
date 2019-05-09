package org.marketdesignresearch.mechlib.mechanisms.ccg;

import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;

public interface CCGMechanismFactory extends MechanismFactory {
    @Override
    CCGAuction getMechanism(Bids bids);

    void setReferencePoint(AuctionResult cachedReferencePoint);

}
