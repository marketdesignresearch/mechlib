package org.marketdesignresearch.mechlib.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ToString @EqualsAndHashCode
@Slf4j
public final class Bundle {
    public static Bundle EMPTY = new Bundle(new HashMap<>());

    @Getter
    private final HashSet<BundleEntry> bundleEntries;

    public Bundle(Map<? extends Good, Integer> map) {
        bundleEntries = new HashSet<>();
        map.forEach((k, v) -> bundleEntries.add(new BundleEntry(k, v)));
    }

    public static Bundle singleGoods(Set<? extends Good> bundle) {
        return new Bundle(bundle.stream().collect(Collectors.toMap(g -> g, g -> 1)));
    }

    public boolean isSingleGood() {
        return bundleEntries.size() == 1 && bundleEntries.iterator().next().getAmount() == 1;
    }

    public Good getSingleGood() {
        if (isSingleGood()) {
            return bundleEntries.iterator().next().getGood();
        } else {
            log.warn("Bundle {} does not only contain one good, thus" +
                    "you can't treat it as a single good bundle.", toString());
            return null;
        }
    }
}
