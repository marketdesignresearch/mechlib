package ch.uzh.ifi.ce.mechanisms.cca.supplementaryround;

import ch.uzh.ifi.ce.domain.Bid;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.BundleBid;
import ch.uzh.ifi.ce.domain.Good;
import ch.uzh.ifi.ce.mechanisms.cca.CCAuction;
import ch.uzh.ifi.ce.mechanisms.cca.Price;
import ch.uzh.ifi.ce.mechanisms.cca.demandquery.DemandQuery;
import com.google.common.collect.Sets;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class ProfitMaximizingSupplementaryRound implements SupplementaryRound {

    private static final int DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS = 500;

    @Setter
    private int numberOfSupplementaryBids = DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS;
    private Map<Good, Price> prices;
    private DemandQuery demandQuery;

    public ProfitMaximizingSupplementaryRound(CCAuction cca) {
        this(cca, true);
    }

    public ProfitMaximizingSupplementaryRound(CCAuction cca, boolean useLastDemandedPrices) {
        this.prices = useLastDemandedPrices ? cca.getLastDemandedPrices() : cca.getPrices();
        this.demandQuery = cca.getDemandQuery();
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
