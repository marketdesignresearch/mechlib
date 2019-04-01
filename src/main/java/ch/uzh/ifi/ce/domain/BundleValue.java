package ch.uzh.ifi.ce.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Class that represents a Value of one {@link Bidder} on one bundle of
 * {@link Good}s in a Mechanism.</p> The object is immutable compareTo, equals
 * and hashCode are all based on the id.
 * 
 * @author Benedikt Buenz
 * 
 */

@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class BundleValue implements Comparable<BundleValue>, Serializable {
    public static final BundleValue ZERO = new BundleValue(BigDecimal.ZERO, Collections.emptySet(), "ZEROBUNDLE");

    private static final long serialVersionUID = 1037198522505712712L;

    @Getter
    private final BigDecimal amount;
    @Getter
    private final Map<Good, Integer> goodsMap;
    @Getter
    private final String id;

    public BundleValue(BigDecimal amount, Set<Good> bundle, String id) {
        this(amount, bundle.stream().collect(Collectors.toMap(good -> good, good -> 1)), id);
    }

    public long nonDummySize() {
        Predicate<Good> isDummy = Good::isDummyGood;
        return goodsMap.keySet().stream().filter(isDummy.negate()).count();
    }

    public BundleBid toBid(UnaryOperator<BigDecimal> valueToBidFunction) {
        return new BundleBid(valueToBidFunction.apply(amount), goodsMap, id);
    }

    @Override
    public int compareTo(BundleValue o) {
        return Comparator.comparing(BundleValue::getAmount).compare(this, o);
    }

}
