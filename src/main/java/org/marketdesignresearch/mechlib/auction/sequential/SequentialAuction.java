package org.marketdesignresearch.mechlib.auction.sequential;

import com.google.common.collect.Lists;
import org.marketdesignresearch.mechlib.domain.Domain;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.auction.Auction;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.MechanismType;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a sequential auction where for each round, there are only bids placed for a specific good
 */
public class SequentialAuction extends Auction {

    public SequentialAuction(Domain domain, MechanismType mechanismType) {
        super(domain, mechanismType);
    }

    @Override
    public List<? extends Good> nextGoods() {
        return Lists.newArrayList(getDomain().getGoods().get(getRounds()));
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
        for (int i = 0; i < getRounds(); i++) {
            result = result.merge(getAuctionResultAtRound(i));
        }
        return result;
    }
}
