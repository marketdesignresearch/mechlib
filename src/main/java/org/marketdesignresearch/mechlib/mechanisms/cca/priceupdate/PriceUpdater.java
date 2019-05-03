package org.marketdesignresearch.mechlib.mechanisms.cca.priceupdate;

import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.mechanisms.cca.Prices;

import java.util.Map;

public interface PriceUpdater {
    Prices updatePrices(Prices oldPrices, Map<Good, Integer> demand);
}
