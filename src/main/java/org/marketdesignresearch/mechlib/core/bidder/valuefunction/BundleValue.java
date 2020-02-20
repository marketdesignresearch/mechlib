package org.marketdesignresearch.mechlib.core.bidder.valuefunction;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.springframework.data.annotation.PersistenceConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Class that represents an XORValue of one {@link Bidder} on one bundle of
 * {@link Good}s in a mechanism. The object is immutable. compareTo, equals
 * and hashCode are all based on the id.
 * 
 * @author Benedikt Buenz
 * 
 */

@RequiredArgsConstructor(onConstructor = @__({@PersistenceConstructor}))
@EqualsAndHashCode(of = "id")
@ToString
public class BundleValue implements Comparable<BundleValue>, Serializable {
    public static final BundleValue ZERO = new BundleValue(BigDecimal.ZERO, Collections.emptySet(), "ZEROBUNDLE");

    private static final long serialVersionUID = 1037198522505712712L;

    @Getter
    private final BigDecimal amount;
    @Getter
    private final Bundle bundle;
    @Getter
    private final String id;

    public BundleValue(BigDecimal amount, Set<Good> bundle, String id) {
        this(amount, new Bundle(bundle.stream().collect(Collectors.toMap(good -> good, good -> 1))), id);
    }

    public BundleValue(BigDecimal amount, Bundle bundle) {
        this(amount, bundle, UUID.randomUUID().toString());
    }

    public long nonDummySize() {
        Predicate<Good> isDummy = Good::isDummyGood;
        return bundle.getBundleEntries().stream().map(BundleEntry::getGood).filter(isDummy.negate()).count();
    }

    public BundleValuePair toBid(UnaryOperator<BigDecimal> valueToBidFunction) {
        return new BundleValuePair(valueToBidFunction.apply(amount), bundle, id);
    }

    @Override
    public int compareTo(BundleValue o) {
        return Comparator.comparing(BundleValue::getAmount).compare(this, o);
    }

}
