package org.marketdesignresearch.mechlib.core.price;

import org.marketdesignresearch.mechlib.core.Bundle;

import java.util.ArrayList;

/**
 * Prices represent the mapping between some bundle of goods and a certain price.
 */
public interface Prices {

    /**
     * No prices - all price queries will result in {@link Price#ZERO}
     */
    Prices NONE = new LinearPrices(new ArrayList<>());

    /**
     * Gets the price for a certain bundle.
     *
     * @param bundle the bundle
     * @return the price for the bundle
     */
    Price getPrice(Bundle bundle);

}
