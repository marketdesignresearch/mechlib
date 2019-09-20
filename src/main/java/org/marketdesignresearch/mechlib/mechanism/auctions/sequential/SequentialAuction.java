package org.marketdesignresearch.mechlib.mechanism.auctions.sequential;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.marketdesignresearch.mechlib.core.BundleBid;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class represents a sequential auction where for each round, there are only bids placed for a specific good
 */
@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class SequentialAuction extends Auction {

    @PersistenceConstructor
    public SequentialAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
        super(domain, outcomeRuleGenerator);
        setMaxRounds(domain.getGoods().size());
    }

    @Override
    public Map<Bidder, List<Bundle>> restrictedBids() {
        if (finished()) return new HashMap<>();
        Bundle bundle = Bundle.of(Sets.newHashSet(getDomain().getGoods().get(getNumberOfRounds())));
        Map<Bidder, List<Bundle>> map = new HashMap<>();
        getDomain().getBidders().forEach(bidder -> map.put(bidder, Lists.newArrayList(bundle)));
        return map;
    }

    @Override
    public int allowedNumberOfBids() {
        if (finished()) return 0;
        return 1;
    }

    /**
     * The bidder will take into account previous round's allocations, because they are definitive in a
     * sequential auction (not interim, as in other auctions).
     *
     * @param bidder the bidder that's about to propose a bid
     * @return A (currently truthful) bid
     */
    @Override
    public Bid proposeBid(Bidder bidder) {
        Bid bid = new Bid();
        if (allowedNumberOfBids() > 0 && restrictedBids().containsKey(bidder)) {
            Bundle alreadyWon = Bundle.EMPTY;
            for (int i = 0; i < getNumberOfRounds(); i++) {
                alreadyWon = alreadyWon.merge(getOutcomeAtRound(i).getAllocation().allocationOf(bidder).getBundle());
            }
            for (Bundle bundle : restrictedBids().get(bidder)) {
                bid.addBundleBid(new BundleBid(bidder.getValue(bundle, alreadyWon), bundle, UUID.randomUUID().toString()));
            }
        }
        return bid;
    }

    /**
     * Overrides the default method to have outcomes only based on each round's bids
     */
    @Override
    public Outcome getOutcomeAtRound(int index) {
        if (getRound(index).getOutcome() == null) {
            getRound(index).setOutcome(getOutcomeRuleGenerator().getOutcomeRule(getBidsAt(index), getMipInstrumentation()).getOutcome());
        }
        return getRound(index).getOutcome();
    }

    @Override
    public Outcome getOutcome() {
        Outcome result = Outcome.NONE;
        for (int i = 0; i < getNumberOfRounds(); i++) {
            result = result.merge(getOutcomeAtRound(i));
        }
        return result;
    }
}
