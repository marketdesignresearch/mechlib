package ch.uzh.ifi.ce.mechanisms.cca.priceupdate;

import ch.uzh.ifi.ce.domain.Good;
import ch.uzh.ifi.ce.mechanisms.cca.Price;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class DemandDependentPriceUpdate implements PriceUpdater {

    private static final BigDecimal DEFAULT_CONSTANT = BigDecimal.valueOf(1e6);

    @Setter
    private BigDecimal constant = DEFAULT_CONSTANT;
    private int round = 1;

    @Getter
    private Map<Good, Price> lastPrices = new HashMap<>();

    @Override
    public Map<Good, Price> updatePrices(Map<Good, Price> oldPrices, Map<Good, Integer> demand) {
        // Fill the last prices map with initial values
        if (lastPrices.isEmpty()) {
            for (Map.Entry<Good, Price> oldPriceEntry : oldPrices.entrySet()) {
                lastPrices.put(oldPriceEntry.getKey(), oldPriceEntry.getValue());
            }
        }

        Map<Good, Price> newPrices = new HashMap<>();
        for (Map.Entry<Good, Price> oldPriceEntry : oldPrices.entrySet()) {
            Good good = oldPriceEntry.getKey();
            BigDecimal diff = BigDecimal.valueOf(demand.getOrDefault(good, 0) - good.available());
            BigDecimal factor = constant.divide(BigDecimal.valueOf(Math.sqrt(round)), RoundingMode.HALF_UP);
            BigDecimal price = oldPriceEntry.getValue().getAmount().add(factor.multiply(diff));
            newPrices.put(good, new Price(price));

            // Overdemanded
            if (price.compareTo(oldPriceEntry.getValue().getAmount()) > 0) {
                lastPrices.put(good, oldPriceEntry.getValue());
            }
        }

        round++;
        return newPrices;
    }
}
