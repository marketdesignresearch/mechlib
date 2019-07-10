package org.marketdesignresearch.mechlib.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
@Slf4j
public final class Bundle {
    public static Bundle EMPTY = new Bundle(new HashMap<>());

    @Getter
    private final List<BundleEntry> bundleEntries;

    public Bundle(Set<BundleEntry> bundleEntries) {
        Preconditions.checkArgument(
                bundleEntries.stream().map(BundleEntry::getGood).collect(Collectors.toSet()).size() == bundleEntries.size(),
                "Invalid bundle: multiple bundle entries for a good detected.");
        this.bundleEntries = bundleEntries.stream().sorted(Comparator.comparing(be -> be.getGood().getName())).collect(Collectors.toList());
    }

    public Bundle(Map<? extends Good, Integer> map) {
        this(map.entrySet().stream().map(entry -> new BundleEntry(entry.getKey(), entry.getValue())).collect(Collectors.toSet()));
    }

    /**
     * This shortcut creates a bundle with goods at quantity 1, because this is a very frequent use case.
     * To create bundles with more complex goods, use the constructors.
     *
     * @param bundle A list of goods
     * @return A bundle consisting of the goods at quantity 1
     */
    public static Bundle of(List<? extends Good> bundle) {
        return new Bundle(bundle.stream().collect(Collectors.toMap(g -> g, g -> 1)));
    }

    public static Bundle of(Good... goods) {
        return of(Lists.newArrayList(goods));
    }

    public static Bundle of(Set<? extends Good> bundle) {
        return of(Lists.newArrayList(bundle));
    }

    public boolean isSingleGood() {
        return bundleEntries.size() == 1 && bundleEntries.iterator().next().getAmount() == 1;
    }

    public boolean areSingleAvailabilityGoods() {
        return bundleEntries.stream().allMatch(be -> be.getGood().available() == 1);
    }

    public Good getSingleGood() {
        Preconditions.checkState(isSingleGood(),
                "Bundle does not only contain one good, thus you can't treat it as a single good bundle.");
        return bundleEntries.iterator().next().getGood();
    }

    public List<? extends Good> getSingleAvailabilityGoods() {
        Preconditions.checkState(areSingleAvailabilityGoods(), "At least one good in the bundle is not a single availability good.");
        return bundleEntries.stream()
                .map(BundleEntry::getGood)
                .sorted(Comparator.comparing(Good::getName))
                .collect(Collectors.toList());
    }

    public Bundle merge(Bundle other) {
        Set<Good> goods = Sets.union(Sets.newHashSet(getBundleEntries()), Sets.newHashSet(other.getBundleEntries())).stream()
                .map(BundleEntry::getGood).collect(Collectors.toSet());
        Map<Good, Integer> map = new HashMap<>();
        for (Good good : goods) {
            Set<BundleEntry> first = getBundleEntries().stream().filter(entry -> entry.getGood().equals(good)).collect(Collectors.toSet());
            Set<BundleEntry> second = other.getBundleEntries().stream().filter(entry -> entry.getGood().equals(good)).collect(Collectors.toSet());
            map.put(good, first.stream().mapToInt(BundleEntry::getAmount).sum() + second.stream().mapToInt(BundleEntry::getAmount).sum());
        }
        return new Bundle(map);
    }

    public boolean contains(Good good) {
        return bundleEntries.stream().anyMatch(be -> be.getGood().equals(good));
    }

    public int countGood(Good good) {
        return contains(good) ? bundleEntries.stream().filter(be -> be.getGood().equals(good)).mapToInt(BundleEntry::getAmount).sum() : 0;
    }

    public int getTotalAmount() {
        return bundleEntries.stream().mapToInt(BundleEntry::getAmount).sum();
    }

    @Override
    public String toString() {
        if (areSingleAvailabilityGoods()) {
            List<? extends Good> goods = getSingleAvailabilityGoods();
            StringBuilder ids = new StringBuilder();
            boolean first = true;
            for (Good good : goods) {
                if (!first) {
                    ids.append(",");
                }
                first = false;
                ids.append(good.getName());
            }
            return ids.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (BundleEntry entry : bundleEntries) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                sb.append(entry.getGood().getName()).append(":").append(entry.getAmount());
            }
            return sb.toString();
        }
    }

}
