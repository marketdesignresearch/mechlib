package ch.uzh.ifi.ce.mechanisms.cca.round.supplementaryround;

import ch.uzh.ifi.ce.domain.Bid;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.BundleBid;
import ch.uzh.ifi.ce.mechanisms.cca.CCAuction;
import com.google.common.collect.Sets;
import lombok.Setter;

import java.util.List;

public class ProfitMaximizingSupplementaryRound implements SupplementaryRound {

    private static final int DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS = 10;

    @Setter
    private int numberOfSupplementaryBids = DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS;
    private CCAuction cca;

    public ProfitMaximizingSupplementaryRound(CCAuction cca) {
        this.cca = cca;
    }

    @Override
    public Bid getSupplementaryBids(String id, Bidder bidder) {
        List<BundleBid> bestBundleBids = cca.getDemandQuery().getBestBundleBids(id, bidder, cca.getLatestPrices(), numberOfSupplementaryBids);
        return new Bid(Sets.newHashSet(bestBundleBids));
    }

    public ProfitMaximizingSupplementaryRound withNumberOfSupplementaryBids(int numberOfSupplementaryBids) {
        setNumberOfSupplementaryBids(numberOfSupplementaryBids);
        return this;
    }

    @Override
    public String getDescription() {
        return "Profit Maximizing Supplementary round with " + numberOfSupplementaryBids + " bids per bidder";
    }

}
