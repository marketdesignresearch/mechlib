package org.marketdesignresearch.mechlib.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.marketdesignresearch.mechlib.auction.Auction;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.LinearPrices;
import org.marketdesignresearch.mechlib.core.price.Price;
import org.marketdesignresearch.mechlib.core.price.Prices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A domain consists of an ordered list of bidders and goods.
 * Its main purpose is to serve as a starting point for an {@link Auction} of any kind of format.
 * It also includes a getter for the efficient allocation in this domain.
 */
public interface Domain {

    /**
     * Gets the list of bidders.
     *
     * @return the bidders
     */
    List<? extends Bidder> getBidders();

    /**
     * Gets the list of goods.
     *
     * @return the goods
     */
    List<? extends Good> getGoods();

    /**
     * Gets the efficient allocation
     *
     * @return the efficient allocation
     */
    Allocation getEfficientAllocation();

    /**
     * A boolean whether the efficient allocation has been calculated already.
     * The reason for that is that the efficient allocation does not change, thus mostly it will be cached.
     * A cached efficient allocation is quick to retrieve. In some auction formats, you'd want to include this
     * information if you can get it for free (i.e., if it already has been calculated), but don't mind skipping
     * this information when it still has to be calculated (which can, depending on the domain, take a long time).
     * This method helps to decouple the retrieval of the efficient allocation and the check whether it has already
     * been calculated. It defaults to true - large domains can take advantage of this decoupling by overriding this.
     *
     * @return true if the efficient allocation already has been calculated, else false
     */
    default boolean hasEfficientAllocationCalculated() {
        return true;
    }

    /**
     * Gets a bidder instance based on her name.
     *
     * @param name the name of the bidder
     * @return the instance of the bidder
     * @throws IllegalArgumentException if the name is not unique in this domain
     */
    default Bidder getBidder(String name) {
        List<Bidder> bidders = getBidders().stream().filter(bidder -> bidder.getName().equals(name)).collect(Collectors.toList());
        Preconditions.checkArgument(bidders.size() == 1, "{} bidders with the name {} have been found.", bidders.size(), name);
        return bidders.get(0);
    }

    /**
     * Gets a good instance based on its name.
     *
     * @param name the name of the good
     * @return the instance of the good
     * @throws IllegalArgumentException if the name is not unique in this domain
     */
    default Good getGood(String name) {
        List<Good> goods = getGoods().stream().filter(bidder -> bidder.getName().equals(name)).collect(Collectors.toList());
        Preconditions.checkArgument(goods.size() == 1, "{} goods with the name {} have been found.", goods.size(), name);
        return goods.get(0);
    }

    /**
     * By default, we cannot assume much about any bidder.
     * So here, we pretend to know about the valuation of the bidder, taking 10 % of the average
     * of the bidders' values for the single goods as linear prices.
     * Note that in more sophisticated domains, you may want to override this and propose prices
     * based on what an auctioneer might know about the bidders / the market.
     *
     * @return the proposed starting prices
     */
    default Prices proposeStartingPrices() {
        Map<Good, Price> priceMap = new HashMap<>();
        getGoods().forEach(good -> {
            BigDecimal price = BigDecimal.ZERO;
            for (Bidder bidder : getBidders()) {
                price = price.add(bidder.getValue(Bundle.of(Sets.newHashSet(good))));
            }
            price = price.setScale(4, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(getBidders().size()), RoundingMode.HALF_UP) // Average
                    .divide(BigDecimal.TEN, RoundingMode.HALF_UP) // 10%
                    .setScale(2, RoundingMode.HALF_UP);
            priceMap.put(good, new Price(price));
        });
        return new LinearPrices(priceMap);
    }

}
