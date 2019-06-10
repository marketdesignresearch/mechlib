package org.marketdesignresearch.mechlib.values;

import com.google.common.collect.Sets;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.BundleEntry;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.domain.SimpleGood;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bidder.ORBidder;
import org.marketdesignresearch.mechlib.domain.bidder.XORBidder;
import org.marketdesignresearch.mechlib.domain.bidder.value.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.value.ORValue;
import org.marketdesignresearch.mechlib.domain.bidder.value.XORValue;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class ValueTest {
    private static SimpleGood A;
    private static SimpleGood B;
    private static SimpleGood C;
    private static SimpleGood D;

    @BeforeClass
    public static void setUp() {
        A = new SimpleGood("0", 2, false);
        B = new SimpleGood("1", 2, false);
        C = new SimpleGood("2", 2, false);
        D = new SimpleGood("3", 2, false);
    }

    @Test
    public void testXORValue() {
        XORValue value = new XORValue();
        value.addBundleValue(new BundleValue(BigDecimal.valueOf(10),
                new Bundle(Sets.newHashSet(new BundleEntry(A, 1)))));
        value.addBundleValue(new BundleValue(BigDecimal.valueOf(11),
                new Bundle(Sets.newHashSet(new BundleEntry(B, 1)))));
        value.addBundleValue(new BundleValue(BigDecimal.valueOf(12),
                new Bundle(Sets.newHashSet(new BundleEntry(C, 1)))));
        value.addBundleValue(new BundleValue(BigDecimal.valueOf(13),
                new Bundle(Sets.newHashSet(new BundleEntry(D, 2)))));
        value.addBundleValue(new BundleValue(BigDecimal.valueOf(20),
                new Bundle(Sets.newHashSet(new BundleEntry(B, 1), new BundleEntry(C, 1)))));
        Bidder bidder = new XORBidder("bidder", value);

        checkValue(bidder, 10, A);
        checkValue(bidder, 11, B);
        checkValue(bidder, 12, C);
        checkValue(bidder, 13, new BundleEntry(D, 2));

        checkValue(bidder, 0, new BundleEntry(A, 2));
        checkValue(bidder, 0, new BundleEntry(B, 2));
        checkValue(bidder, 0, new BundleEntry(C, 2));
        checkValue(bidder, 0, new BundleEntry(D, 1));

        checkValue(bidder, 0, A, B);
        checkValue(bidder, 20, B, C);
        checkValue(bidder, 0, A, C);
        checkValue(bidder, 0, A, B, C);
    }

    @Test
    public void testORValue() {
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

        checkValue(bidder, 10, A);
        checkValue(bidder, 11, B);
        checkValue(bidder, 12, C);
        checkValue(bidder, 13, new BundleEntry(D, 2)); // FIXME

        checkValue(bidder, 10, new BundleEntry(A, 2));
        checkValue(bidder, 11, new BundleEntry(B, 2));
        checkValue(bidder, 12, new BundleEntry(C, 2));
        checkValue(bidder, 13, new BundleEntry(D, 1));

        checkValue(bidder, 21, A, B);
        checkValue(bidder, 22, A, C);
        checkValue(bidder, 23, A, D);
        checkValue(bidder, 23, B, C);
        checkValue(bidder, 24, B, D);
        checkValue(bidder, 25, C, D);
        checkValue(bidder, 33, A, B, C);
        checkValue(bidder, 34, A, B, D);
        checkValue(bidder, 35, A, C, D);
        checkValue(bidder, 36, B, C, D);
        checkValue(bidder, 46, A, B, C, D);
    }

    private void checkValue(Bidder bidder, int amount, BundleEntry... bundleEntries) {
        assertThat(bidder.getValue(new Bundle(Sets.newHashSet(bundleEntries))))
                .isEqualTo(BigDecimal.valueOf(amount));
    }

    private void checkValue(Bidder bidder, int amount, Good... goods) {
        Set<BundleEntry> bundleEntries = new HashSet<>();
        for (Good good : goods) bundleEntries.add(new BundleEntry(good, 1));
        assertThat(bidder.getValue(new Bundle(Sets.newHashSet(bundleEntries))))
                .isEqualTo(BigDecimal.valueOf(amount));
    }
}
