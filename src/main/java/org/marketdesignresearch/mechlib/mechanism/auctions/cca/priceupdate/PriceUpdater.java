package org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate;

import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.core.Good;

import java.util.Map;

public interface PriceUpdater {
    Prices updatePrices(Prices oldPrices, Map<Good, Integer> demand);
}
