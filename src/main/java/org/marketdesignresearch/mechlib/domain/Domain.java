package org.marketdesignresearch.mechlib.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.price.LinearPrices;
import org.marketdesignresearch.mechlib.domain.price.Price;
import org.marketdesignresearch.mechlib.domain.price.Prices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface Domain {

    List<? extends Bidder> getBidders();
    List<? extends Good> getGoods();
    Allocation getEfficientAllocation();

    default boolean hasEfficientAllocationCalculated() {
        return true;
    }

    default Bidder getBidder(String name) {
        List<Bidder> bidders = getBidders().stream().filter(bidder -> bidder.getName().equals(name)).collect(Collectors.toList());
        Preconditions.checkArgument(bidders.size() == 1);
        return bidders.get(0);
    }

    default Good getGood(String name) {
        List<Good> goods = getGoods().stream().filter(bidder -> bidder.getName().equals(name)).collect(Collectors.toList());
        Preconditions.checkArgument(goods.size() == 1);
        return goods.get(0);
    }

     /**
     * By default, we cannot assume much about any bidder.
     * So here, we pretend to know about the valuation of the bidder, taking 10 % of the average
     * of the bidders' values for the single goods as linear prices.
     * Note that in more sophisticated domains, you may want to override this and propose prices
     * based on what an auctioneer might know about the bidders / the market.
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
