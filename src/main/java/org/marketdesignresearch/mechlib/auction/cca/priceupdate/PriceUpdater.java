package org.marketdesignresearch.mechlib.auction.cca.priceupdate;

import org.marketdesignresearch.mechlib.domain.price.Prices;
import org.marketdesignresearch.mechlib.domain.Good;

import java.util.Map;

public interface PriceUpdater {
    Prices updatePrices(Prices oldPrices, Map<Good, Integer> demand);
}
