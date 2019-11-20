package org.marketdesignresearch.mechlib.mechanism.auctions.simultaneous;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class represents a sequential auction where for each round, there are only bids placed for a specific good
 */
@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class SimultaneousAuction extends Auction {

    @PersistenceConstructor
    public SimultaneousAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
        super(domain, outcomeRuleGenerator);
    }

    @Override
    public Map<Bidder, List<Bundle>> restrictedBids() {
        if (finished()) return new HashMap<>();
        Map<Bidder, List<Bundle>> restrictedBids = new HashMap<>();
        List<Bundle> bundleList = getDomain().getGoods().stream().map(Bundle::of).collect(Collectors.toList());
        getDomain().getBidders().forEach(bidder -> restrictedBids.put(bidder, bundleList));
        return restrictedBids;
    }
}
