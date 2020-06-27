package org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.price.LinearPrices;
import org.marketdesignresearch.mechlib.core.price.Price;
import org.marketdesignresearch.mechlib.core.price.Prices;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class DemandDependentPriceUpdate implements PriceUpdater {

    private static final BigDecimal DEFAULT_CONSTANT = BigDecimal.valueOf(1e6);

    @Setter
    private BigDecimal constant = DEFAULT_CONSTANT;
    private int round = 1;

    @Override
    public Prices updatePrices(Prices oldPrices, Map<Good, Integer> demand) {
        Preconditions.checkArgument(oldPrices instanceof LinearPrices, "Demand dependent price updater only works with linear prices.");
        Map<Good, Price> newPrices = new LinkedHashMap<>();
        for (Map.Entry<Good, Integer> entry : demand.entrySet()) {
            Good good = entry.getKey();
            BigDecimal diff = BigDecimal.valueOf(demand.getOrDefault(good, 0) - good.getQuantity());
            BigDecimal factor = constant.divide(BigDecimal.valueOf(Math.sqrt(round)), RoundingMode.HALF_UP);
            BigDecimal price = oldPrices.getPrice(Bundle.of(good)).getAmount().add(factor.multiply(diff));
            newPrices.put(good, new Price(price));
        }

        round++;
        return new LinearPrices(newPrices);
    }
}
