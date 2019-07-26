package org.marketdesignresearch.mechlib.domain.bidder;


import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.bidder.strategy.Strategy;
import org.marketdesignresearch.mechlib.domain.price.Prices;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public interface Bidder {

    UUID getId();
    BigDecimal getValue(Bundle bundle);
    List<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles, boolean allowNegative, double relPoolTolerance, double absPoolTolerance, double poolTimeLimit);

    default List<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles, boolean allowNegative) {
        return getBestBundles(prices, maxNumberOfBundles, allowNegative, 0.0, 0.0, -1);
    }

    default String getName() {
        return getId().toString();
    }

    default String getShortDescription() {
        return getName();
    }

    default String getDescription() {
        return getName();
    }

    default Bundle getBestBundle(Prices prices) {
        List<Bundle> results = getBestBundles(prices, 1);
        if (results.size() > 1) System.err.println("Requested one solution, got " + results.size() + ".");
        return results.get(0);
    }

    default List<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles) {
        List<Bundle> results = getBestBundles(prices, maxNumberOfBundles, false);
        if (results.size() < 1) results.add(Bundle.EMPTY);
        return results;
    }

    default BigDecimal getUtility(Bundle bundle, Prices prices) {
        return getValue(bundle).subtract(prices.getPrice(bundle).getAmount());
    }

    /* TODO: Include default strategy with bidder
    default Strategy getDefaultStrategy() {
        return Strategy.TRUTHFUL;
    }*/
}
