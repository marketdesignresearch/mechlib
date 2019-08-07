package org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate;

import com.google.common.base.Preconditions;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.price.LinearPrices;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.price.Price;
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
        Preconditions.checkArgument(oldPrices instanceof LinearPrices, "Simple relative price updater only works with linear prices.");

        Map<Good, Price> newPrices = new HashMap<>();

        for (Map.Entry<Good, Integer> entry : demand.entrySet()) {
            Good good = entry.getKey();
            Price oldPrice = oldPrices.getPrice(Bundle.of(good));

            if (good.getQuantity() < entry.getValue()) {
                if (oldPrice.equals(Price.ZERO))
                    newPrices.put(good, new Price(initialUpdate));
                else
                    newPrices.put(good, new Price(oldPrice.getAmount().add(oldPrice.getAmount().multiply(priceUpdate))));
            } else {
                newPrices.put(good, oldPrice);
            }

        }

        return new LinearPrices(newPrices);
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
