package org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate;

import java.util.Map;

import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.price.Prices;

/**
 * Price updater for the clock phase of the combinatorial clock auction.
 * @author Fabio Isler
 */
public interface PriceUpdater {
	Prices updatePrices(Prices oldPrices, Map<Good, Integer> demand);
}
