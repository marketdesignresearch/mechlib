package org.marketdesignresearch.mechlib.auction.cca.priceupdate;

import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.core.Good;

import java.util.Map;

public interface PriceUpdater {
    Prices updatePrices(Prices oldPrices, Map<Good, Integer> demand);
}
