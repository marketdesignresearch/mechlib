package org.marketdesignresearch.mechlib.utils;

import java.math.BigDecimal;
import java.util.Comparator;

public class PrecisionUtils {
    public static  BigDecimal EPSILON = BigDecimal.valueOf(1e-6);

    public static <T extends Comparable<T>> T max(T o1, T o2) {
        return o1.compareTo(o2) >= 0 ? o1 : o2;
    }

    public static <T extends Comparable<T>> T min(T o1, T o2) {
        return o1.compareTo(o2) <= 0 ? o1 : o2;
    }

    public static <T> T max(T o1, T o2, Comparator<T> comparator) {
        return comparator.compare(o1, o2) >= 0 ? o1 : o2;
    }

    public static boolean fuzzyEquals(BigDecimal a, BigDecimal b, BigDecimal tolerance) {
        assert tolerance.signum() >= 0;
        return a.subtract(b).abs().compareTo(tolerance) <= 0;
    }

    public static int fuzzyCompare(BigDecimal a, BigDecimal b, BigDecimal tolerance) {
        if (fuzzyEquals(a, b, tolerance)) {
            return 0;
        } else {
            return a.compareTo(b);
        }
    }
}
