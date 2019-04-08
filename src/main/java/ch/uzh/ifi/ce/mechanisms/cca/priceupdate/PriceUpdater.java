package ch.uzh.ifi.ce.mechanisms.cca.priceupdate;

import ch.uzh.ifi.ce.domain.Good;
import ch.uzh.ifi.ce.mechanisms.cca.Price;
import ch.uzh.ifi.ce.mechanisms.cca.Prices;

import java.util.Map;

public interface PriceUpdater {
    Prices updatePrices(Prices oldPrices, Map<Good, Integer> demand);
}
