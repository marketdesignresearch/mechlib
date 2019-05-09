package org.marketdesignresearch.mechlib.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Bundle extends HashMap<Good, Integer> {

    public static Bundle EMPTY = new Bundle(new HashMap<>());

    public Bundle(Map<Good, Integer> map) {
        super(map);
    }

    public static Bundle singleGoods(Set<Good> bundle) {
        return new Bundle(bundle.stream().collect(Collectors.toMap(g -> g, g -> 1)));
    }

    public boolean isSingleGood() {
        return size() == 1 && values().iterator().next() == 1;
    }

    public Good getSingleGood() {
        if (isSingleGood()) {
            return keySet().iterator().next();
        } else {
            throw new UnsupportedOperationException("Bundle " + toString() + " does not only contain one good, thus" +
                    "you can't treat it as a single good bundle.");
        }
    }

    @Override
    public String toString() {
        return "Bundle(" + super.toString() + ")";
    }
}
