package org.marketdesignresearch.mechlib.core.bundlesampling;

import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * This class samples a random bundle from a list of goods.
 * Each good as a 50% chance to be included in the bundle. Thus, if a good has a quantity of 2, the chance that
 * at least one of the individual goods are included is already at 75%.
 * No information is preserved, there is no guarantee that you can't get the same bundle twice.
 */
@RequiredArgsConstructor
public class UniformRandomBundleSampling implements BundleSampling {

    public UniformRandomBundleSampling() {
        this(new Random());
    }

    private final Random random;

    @Override
    public Bundle getSingleBundle(List<? extends Good> goods) {
        Set<BundleEntry> bundleEntries = new HashSet<>();
        for (Good g : goods) {
            int amount = (int) Math.floor(random.nextDouble() * (g.getQuantity() + 1));
            if (amount > 0)
                bundleEntries.add(new BundleEntry(g, amount));
        }
        return new Bundle(bundleEntries);
    }
}
