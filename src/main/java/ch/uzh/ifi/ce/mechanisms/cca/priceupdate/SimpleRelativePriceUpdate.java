package ch.uzh.ifi.ce.mechanisms.cca.priceupdate;

import ch.uzh.ifi.ce.domain.Good;
import ch.uzh.ifi.ce.mechanisms.cca.Price;
import ch.uzh.ifi.ce.mechanisms.cca.Prices;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SimpleRelativePriceUpdate implements PriceUpdater {

    private static final BigDecimal DEFAULT_PRICE_UPDATE = BigDecimal.valueOf(0.1);
    private static final BigDecimal DEFAULT_INITIAL_UPDATE = BigDecimal.valueOf(1e5);

    @Setter
    private BigDecimal priceUpdate = DEFAULT_PRICE_UPDATE;
    @Setter
    private BigDecimal initialUpdate = DEFAULT_INITIAL_UPDATE;

    @Override
    public Prices updatePrices(Prices oldPrices, Map<Good, Integer> demand) {

        Map<Good, Price> newPrices = new HashMap<>();

        for (Map.Entry<Good, Price> oldPriceEntry : oldPrices.entrySet()) {
            Good good = oldPriceEntry.getKey();
            if (good.available() < demand.getOrDefault(good, 0)) {
                if (oldPriceEntry.getValue().equals(Price.ZERO))
                    newPrices.put(good, new Price(initialUpdate));
                else
                    newPrices.put(good, new Price(oldPriceEntry.getValue().getAmount().add(oldPriceEntry.getValue().getAmount().multiply(priceUpdate))));
            } else {
                newPrices.put(good, oldPriceEntry.getValue());
            }

        }

        return new Prices(newPrices);
    }

    public SimpleRelativePriceUpdate withPriceUpdate(BigDecimal priceUpdate) {
        setPriceUpdate(priceUpdate);
        return this;
    }

    public SimpleRelativePriceUpdate withInitialUpdate(BigDecimal initialUpdate) {
        setInitialUpdate(initialUpdate);
        return this;
    }
}
