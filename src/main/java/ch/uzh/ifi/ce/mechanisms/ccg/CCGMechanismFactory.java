package ch.uzh.ifi.ce.mechanisms.ccg;

import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.domain.AuctionResult;

public interface CCGMechanismFactory extends MechanismFactory {
    @Override
    CCGAuction getMechanism(AuctionInstance auctionInstance);

    void setReferencePoint(AuctionResult cachedReferencePoint);

}
