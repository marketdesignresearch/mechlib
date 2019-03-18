package ch.uzh.ifi.ce.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;

public class AuctionInstance {
    private final Bids bids;
    private final Set<Good> goods;

    public AuctionInstance(Bids bids, Set<Good> goods) {
        this.bids = bids;
        this.goods = Collections.unmodifiableSet(goods);
    }

    public Set<Bidder> getBidders() {
        return bids.getBidders();
    }

    public Collection<Bid> getBidCollection() {
        return bids.getBids();
    }

    public Bids getBids() {
        return bids;
    }

    /**
     * 
     * @param bidder to be removed
     * @return A new auction without the specified bidder
     */
    public AuctionInstance without(Bidder bidder) {

        return new AuctionInstance(bids.without(bidder), goods);

    }

    /**
     *
     * @param bidders to be included
     * @return A new auction including only the specified bidders
     */
    public AuctionInstance of(Set<Bidder> bidders) {

        return new AuctionInstance(bids.of(bidders), goods);

    }

    public Set<Good> getGoods() {
        return goods;
    }

    // TODO: Does not work with OR*
    /**
     * 
     * @return An auction where all bids have been reduced by the payoff of
     *         auctionResult
     */
    public AuctionInstance reducedBy(AuctionResult auctionResult) {
        Bids newBids = new Bids();
        for (Entry<Bidder, Bid> bidder : bids) {
            BigDecimal payoff = auctionResult.payoffOf(bidder.getKey());
            newBids.setBid(bidder.getKey(), bidder.getValue().reducedBy(payoff));
        }
        return new AuctionInstance(newBids, goods);
    }

    public Bid getBid(Bidder bidder) {
        return bids.getBid(bidder);
    }

}
