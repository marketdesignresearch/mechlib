package ch.uzh.ifi.ce.mechanisms.ccg;

import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.mechanisms.AuctionMechanism;

public interface MechanismFactory {

    AuctionMechanism getMechanism(AuctionInstance auctionInstance);

    String getMechanismName();
}
