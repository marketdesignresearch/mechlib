package org.marketdesignresearch.mechlib.core.bidder;


import edu.harvard.econcs.jopt.solver.SolveParam;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.InteractionStrategy;
import org.marketdesignresearch.mechlib.core.bidder.strategy.Strategy;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentationable;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.Interaction;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


/**
 * A bidder represents an agent in a mechanism.
 * <br>
 * A bidder at least has an ID, and can be given a name, a short description, and a description.
 * <br>
 * Most importantly, a bidder mostly has a {@link ValueFunction} of any form attached that defines her values for
 * bundles of goods.
 * <br>
 * A bidder can answer two types of questions:
 * <ol>
 *     <li>Value queries: What's this bidder's value for a certain bundle?</li>
 *     <li>Demand queries: Given a set of {@link Prices}, what is/are the profit maximizing bundle(s) of this bidder?</li>
 * </ol>
 * <br>
 * Lastly, a bidder has a default {@link Strategy} based on which she would turn her true valuations into bids.
 */
public interface Bidder extends MipInstrumentationable {

    /**
     * Gets the bidder's id.
     *
     * @return the id
     */
    UUID getId();

    /**
     * Asks the bidder a value query: What is your value for a certain bundle of goods
     *
     * @param bundle the bundle
     * @return the value of this bidder for the bundle of goods
     */
    BigDecimal getValue(Bundle bundle);

    /**
     * Asks the bidder a value query: What is your value for a certain bundle of goods,
     * given a bundle that is assured to be already won?
     *
     * @param bundle the bundle
     * @param alreadyWon the bundle which the bidder already won
     * @return the value of this bidder for the bundle of goods
     */
    default BigDecimal getValue(Bundle bundle, Bundle alreadyWon) {
        return BigDecimal.ZERO.max(getValue(bundle.merge(alreadyWon, true)).subtract(getValue(alreadyWon)));
    }

    /**
     * Asks the bidder a demand query: What bundles would you choose if you'd have to pay certain prices?
     *
     * @param prices             the prices
     * @param maxNumberOfBundles the maximum number of bundles to report
     * @param allowNegative      whether or not to also report bundles when the utility of paying the given prices for the bundle would be negative
     *
     * If {@link WinnerDetermination.PoolMode#MODE_4} is used, the following parameters define some JOpt parameters linked to the solution pool:
     * @param relPoolTolerance   the relative pool tolerance: {@link SolveParam#SOLUTION_POOL_MODE_4_RELATIVE_GAP_TOLERANCE}
     * @param absPoolTolerance   the absolute pool tolerance: {@link SolveParam#SOLUTION_POOL_MODE_4_ABSOLUTE_GAP_TOLERANCE}
     * @param poolTimeLimit      the pool time limit: {@link SolveParam#SOLUTION_POOL_MODE_4_TIME_LIMIT}
     * @return A list of best bundles
     */
    List<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles, boolean allowNegative, double relPoolTolerance, double absPoolTolerance, double poolTimeLimit);

    /**
     * Asks the bidder a demand query, without any pool tolerances or time limit.
     * @see #getBestBundles(Prices, int, boolean, double, double, double)
     */
    default List<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles, boolean allowNegative) {
        return getBestBundles(prices, maxNumberOfBundles, allowNegative, 0.0, 0.0, -1);
    }

    /**
     * Asks a bidder a demand query, without any pool tolerances or time limit, not accepting negative utility
     * @see #getBestBundles(Prices, int, boolean)
     */
    default List<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles) {
        List<Bundle> results = getBestBundles(prices, maxNumberOfBundles, false);
        if (results.size() < 1) results.add(Bundle.EMPTY);
        return results;
    }

    /**
     * Asks the bidder a demand query for a single bundle. Pool tolerances, pool time limit and allowing negative
     * utility is not needed in this case.
     * @see #getBestBundles(Prices, int)
     *
     * @param prices the prices
     * @return the best bundle
     */
    default Bundle getBestBundle(Prices prices) {
        List<Bundle> results = getBestBundles(prices, 1);
        if (results.size() > 1) System.err.println("Requested one solution, got " + results.size() + ".");
        return results.get(0);
    }

    /**
     * Gets the bidder's name. If not overridden, this is equal to the ID as a string.
     *
     * @return the bidder's name
     */
    default String getName() {
        return getId().toString();
    }

    /**
     * Gets the bidder's short description. If not overridden, this is equal to the name.
     *
     * @return the bidder's short description
     */
    default String getShortDescription() {
        return getName();
    }

    /**
     * Gets the bidder's description. If not overridden, this is equal to the short description.
     *
     * @return the bidder's description
     */
    default String getDescription() {
        return getShortDescription();
    }

    /**
     * A helper method that returns a bidder's utility for a bundle given certain prices.
     *
     * @param bundle the bundle
     * @param prices the prices
     * @return the utility
     */
    default BigDecimal getUtility(Bundle bundle, Prices prices) {
        return getValue(bundle).subtract(prices.getPrice(bundle).getAmount());
    }

    default <T extends InteractionStrategy> T getStrategy(Class<T> type) {
    	return InteractionStrategy.defaultStrategy(type, this);
    }
}
