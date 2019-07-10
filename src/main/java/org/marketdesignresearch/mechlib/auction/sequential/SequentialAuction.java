package org.marketdesignresearch.mechlib.auction.sequential;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.marketdesignresearch.mechlib.auction.Auction;
import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.Domain;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.MechanismType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a sequential auction where for each round, there are only bids placed for a specific good
 */
public class SequentialAuction extends Auction {

    public SequentialAuction(Domain domain, MechanismType mechanismType) {
        super(domain, mechanismType);
        setMaxRounds(domain.getGoods().size());
    }

    @Override
    public Map<Bidder, List<Bundle>> restrictedBids() {
        Bundle bundle = Bundle.of(Sets.newHashSet(getDomain().getGoods().get(getNumberOfRounds())));
        Map<Bidder, List<Bundle>> map = new HashMap<>();
        getDomain().getBidders().forEach(bidder -> map.put(bidder, Lists.newArrayList(bundle)));
        return map;
    }

    @Override
    public int allowedNumberOfBids() {
        return 1;
    }

    /**
     * Overrides the default method to have mechanism results only based on each round's bids
     */
    @Override
    public MechanismResult getAuctionResultAtRound(int index) {
        if (getRound(index).getMechanismResult() == null) {
            getRound(index).setMechanismResult(getMechanismType().getMechanism(getBidsAt(index)).getMechanismResult());
        }
        return getRound(index).getMechanismResult();
    }

    @Override
    public MechanismResult getMechanismResult() {
        MechanismResult result = MechanismResult.NONE;
        for (int i = 0; i < getNumberOfRounds(); i++) {
            result = result.merge(getAuctionResultAtRound(i));
        }
        return result;
    }
}
