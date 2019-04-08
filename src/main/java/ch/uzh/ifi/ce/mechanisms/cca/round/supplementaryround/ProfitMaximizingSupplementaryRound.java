package ch.uzh.ifi.ce.mechanisms.cca.round.supplementaryround;

import ch.uzh.ifi.ce.domain.Bid;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.BundleBid;
import ch.uzh.ifi.ce.mechanisms.cca.CCAuction;
import ch.uzh.ifi.ce.mechanisms.cca.Prices;
import ch.uzh.ifi.ce.demandquery.DemandQuery;
import com.google.common.collect.Sets;
import lombok.Setter;

import java.util.List;

public class ProfitMaximizingSupplementaryRound implements SupplementaryRound {

    private static final int DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS = 500;

    @Setter
    private int numberOfSupplementaryBids = DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS;
    private Prices prices;
    private DemandQuery demandQuery;

    public ProfitMaximizingSupplementaryRound(CCAuction auction) {
        this.prices = auction.getLatestPrices();
        this.demandQuery = auction.getDemandQuery();
    }

    @Override
    public Bid getSupplementaryBids(Bidder bidder) {
        List<BundleBid> bestBundleBids = demandQuery.getBestBundleBids(bidder, prices, numberOfSupplementaryBids);
        return new Bid(Sets.newHashSet(bestBundleBids));
    }

    public ProfitMaximizingSupplementaryRound withNumberOfSupplementaryBids(int numberOfSupplementaryBids) {
        setNumberOfSupplementaryBids(numberOfSupplementaryBids);
        return this;
    }

}
