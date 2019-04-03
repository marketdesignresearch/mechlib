package ch.uzh.ifi.ce.mechanisms.cca.priceupdate;

import ch.uzh.ifi.ce.domain.Good;
import ch.uzh.ifi.ce.mechanisms.cca.Price;

import java.util.Map;

public interface PriceUpdater {
    Map<Good, Price> updatePrices(Map<Good, Price> oldPrices, Map<Good, Integer> demand);
    Map<Good, Price> getLastPrices();
}
