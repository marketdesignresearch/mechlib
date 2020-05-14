package org.marketdesignresearch.mechlib.mechanism.auctions.simultaneous;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This class represents a sequential auction where for each round, there are only bids placed for a specific good
 * 
 * TODO change to new auction design with interactions 
 */
/*@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class SimultaneousAuction extends Auction<BundleExactValuePair> {

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
*/