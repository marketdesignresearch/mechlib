package org.marketdesignresearch.mechlib.auction.cca.priceupdate;

import com.google.common.base.Preconditions;
import org.marketdesignresearch.mechlib.core.price.Price;
import org.marketdesignresearch.mechlib.core.price.LinearPrices;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.core.Good;
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
        Preconditions.checkArgument(oldPrices instanceof LinearPrices, "Demand dependent price updater only works with linear prices.");
        LinearPrices oldLinearPrices = (LinearPrices) oldPrices;
        Map<Good, Price> newPrices = new HashMap<>();
        for (Map.Entry<Good, Price> oldPriceEntry : oldLinearPrices.entrySet()) {
            Good good = oldPriceEntry.getKey();
            BigDecimal diff = BigDecimal.valueOf(demand.getOrDefault(good, 0) - good.getQuantity());
            BigDecimal factor = constant.divide(BigDecimal.valueOf(Math.sqrt(round)), RoundingMode.HALF_UP);
            BigDecimal price = oldPriceEntry.getValue().getAmount().add(factor.multiply(diff));
            newPrices.put(good, new Price(price));
        }

        round++;
        return new LinearPrices(newPrices);
    }
}
