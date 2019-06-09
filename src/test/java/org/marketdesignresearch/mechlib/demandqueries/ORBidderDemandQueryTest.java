package org.marketdesignresearch.mechlib.demandqueries;

import com.google.common.collect.Sets;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.BundleEntry;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.domain.SimpleGood;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bidder.ORBidder;
import org.marketdesignresearch.mechlib.domain.bidder.value.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.value.ORValue;
import org.marketdesignresearch.mechlib.domain.price.LinearPrices;
import org.marketdesignresearch.mechlib.domain.price.Price;
import org.marketdesignresearch.mechlib.domain.price.Prices;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class ORBidderDemandQueryTest {
    private static SimpleGood A;
    private static SimpleGood B;
    private static SimpleGood C;
    private static SimpleGood D;
    private static Prices prices;


    @BeforeClass
    public static void setUp() {

        A = new SimpleGood("0", 2, false); // 10 - 8 = 2
        B = new SimpleGood("1", 2, false); // 11 - 7 = 4
        C = new SimpleGood("2", 2, false); // 12 - 7 = 5
        D = new SimpleGood("3", 2, false); // 13 - 35 = -22
        Map<Good, Price> priceMap = new HashMap<>();
        priceMap.put(A, Price.of(8));
        priceMap.put(B, Price.of(7));
        priceMap.put(C, Price.of(7));
        priceMap.put(D, Price.of(35));
        prices = new LinearPrices(priceMap);
    }

    @Test
    public void testDemandQuery() {
        ORValue value = new ORValue();
        value.addBundleValue(new BundleValue(BigDecimal.valueOf(10),
                new Bundle(Sets.newHashSet(new BundleEntry(A, 1)))));
        value.addBundleValue(new BundleValue(BigDecimal.valueOf(11),
                new Bundle(Sets.newHashSet(new BundleEntry(B, 1)))));
        value.addBundleValue(new BundleValue(BigDecimal.valueOf(12),
                new Bundle(Sets.newHashSet(new BundleEntry(C, 1)))));
        value.addBundleValue(new BundleValue(BigDecimal.valueOf(9),
                new Bundle(Sets.newHashSet(new BundleEntry(D, 1)))));
        value.addBundleValue(new BundleValue(BigDecimal.valueOf(13),
                new Bundle(Sets.newHashSet(new BundleEntry(D, 1)))));
        Bidder bidder = new ORBidder("bidder", value);

        Bundle bestBundle = bidder.getBestBundle(prices);
        List<Bundle> bestBundles = bidder.getBestBundles(prices, 10);


        assertThat(bestBundles).hasSize(8);
        assertThat(bestBundle).isEqualTo(bestBundles.get(0));

        checkBundle(bestBundles.get(0), A, B, C   );
        checkBundle(bestBundles.get(1),    B, C   );
        checkBundle(bestBundles.get(2), A,    C   );
        checkBundle(bestBundles.get(3), A, B      );
        checkBundle(bestBundles.get(4),       C   );
        checkBundle(bestBundles.get(5),    B      );
        checkBundle(bestBundles.get(6), A         );
        checkBundle(bestBundles.get(7)            );

        List<Bundle> bestBundlesInclNegative = bidder.getBestBundles(prices, 10, true);
        assertThat(bestBundlesInclNegative).hasSize(10);
        assertThat(bestBundles.subList(0, 8).equals(bestBundlesInclNegative));
        checkBundle(bestBundlesInclNegative.get(8), A, B, C, D);
        checkBundle(bestBundlesInclNegative.get(9),    B, C, D);

    }

    private void checkBundle(Bundle bundle, Good... goods) {
        Set<Good> goodSet = Sets.newHashSet(goods);
        for (Good good : Sets.newHashSet(A, B, C, D)) {
            if (goodSet.contains(good)) {
                assertThat(bundle.getBundleEntries()).contains(new BundleEntry(good, 1));
            } else {
                assertThat(bundle.getBundleEntries()).doesNotContain(new BundleEntry(good, 1));
            }
        }
    }

}
