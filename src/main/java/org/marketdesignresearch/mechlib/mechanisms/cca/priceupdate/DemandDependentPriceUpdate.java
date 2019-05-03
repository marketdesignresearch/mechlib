package org.marketdesignresearch.mechlib.mechanisms.cca.priceupdate;

import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.mechanisms.cca.Price;
import org.marketdesignresearch.mechlib.mechanisms.cca.Prices;
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

    @Override
    public Prices updatePrices(Prices oldPrices, Map<Good, Integer> demand) {

        Map<Good, Price> newPrices = new HashMap<>();
        for (Map.Entry<Good, Price> oldPriceEntry : oldPrices.entrySet()) {
            Good good = oldPriceEntry.getKey();
            BigDecimal diff = BigDecimal.valueOf(demand.getOrDefault(good, 0) - good.available());
            BigDecimal factor = constant.divide(BigDecimal.valueOf(Math.sqrt(round)), RoundingMode.HALF_UP);
            BigDecimal price = oldPriceEntry.getValue().getAmount().add(factor.multiply(diff));
            newPrices.put(good, new Price(price));
        }

        round++;
        return new Prices(newPrices);
    }
}
