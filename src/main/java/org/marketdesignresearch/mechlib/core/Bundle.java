package org.marketdesignresearch.mechlib.core;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * A bundle is a collection of goods with corresponding quantities.
 * Often, in simpler domains, all quantities are equal to one. For that case, this class introduces many
 * convenience methods to create and retrieve such simple bundles.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@PersistenceConstructor}))
@EqualsAndHashCode
@Slf4j
public final class Bundle {
    /**
     * The empty bundle.
     */
    public static Bundle EMPTY = new Bundle(new LinkedHashMap<>());

    @Getter
    private final List<BundleEntry> bundleEntries;

    /**
     * Instantiates a new Bundle.
     *
     * @param bundleEntries the bundle entries
     */
    public Bundle(Set<BundleEntry> bundleEntries) {
        Preconditions.checkArgument(
                bundleEntries.stream().map(BundleEntry::getGood).collect(Collectors.toSet()).size() == bundleEntries.size(),
                "Invalid bundle: multiple bundle entries for a good detected.");
        Preconditions.checkArgument(
                bundleEntries.stream().map(be -> be.getGood().getName()).collect(Collectors.toSet()).size() == bundleEntries.size(),
                "Invalid bundle: Some included goods have the same name.");
        this.bundleEntries = ImmutableList.copyOf(bundleEntries.stream().sorted(Comparator.comparing(be -> be.getGood().getName())).collect(Collectors.toList()));
    }

    /**
     * Instantiates a new Bundle.
     *
     * @param map a good -> quantity map
     */
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
        return new Bundle(bundle.stream().collect(Collectors.toMap(g -> g, g -> 1, (e1, e2) -> e1, LinkedHashMap::new)));
    }

    /**
     * @see #of(List)
     */
    public static Bundle of(Good... goods) {
        return of(Lists.newArrayList(goods));
    }

    /**
     * @see #of(List)
     */
    public static Bundle of(Set<? extends Good> bundle) {
        return of(Lists.newArrayList(bundle));
    }

    /**
     * Checks if the bundle is consisting of only a single good with a single quantity.
     *
     * @return true if the bundle is consisting of only a single good with a single quantity; else false
     */
    public boolean isSingleGood() {
        return bundleEntries.size() == 1 && bundleEntries.iterator().next().getAmount() == 1;
    }

    /**
     * Checks if all the goods in this bundle are of single quantity
     *
     * @return the boolean
     */
    public boolean areSingleQuantityGoods() {
        return bundleEntries.stream().allMatch(be -> be.getGood().getQuantity() == 1);
    }

    /**
     * Given {@link #isSingleGood()}, gets the good.
     *
     * @return the single good
     */
    public Good getSingleGood() {
        Preconditions.checkState(isSingleGood(),
                "Bundle does not only contain one good, thus you can't treat it as a single good bundle.");
        return bundleEntries.iterator().next().getGood();
    }

    /**
     * Given {@link #areSingleQuantityGoods()}, gets the single quantity goods.
     *
     * @return the single quantity goods
     */
    public List<? extends Good> getSingleQuantityGoods() {
        Preconditions.checkState(areSingleQuantityGoods(), "At least one good in the bundle is not a single quantity good.");
        return bundleEntries.stream()
                .map(BundleEntry::getGood)
                .sorted(Comparator.comparing(Good::getName))
                .collect(Collectors.toList());
    }

    /**
     * Merges this bundle with another bundle.
     *
     * @param other the other bundle
     * @return the merged bundle
     */
    public Bundle merge(Bundle other) {
        return merge(other, false);
    }

    /**
     * Merges this bundle with another bundle.
     *
     * @param other the other bundle
     * @param cutoff whether or not the bundle entries should be cut off at the available quantity
     * @return the merged bundle
     */
    public Bundle merge(Bundle other, boolean cutoff) {
        Set<Good> goods = Sets.union(Sets.newHashSet(getBundleEntries()), Sets.newHashSet(other.getBundleEntries())).stream()
                .map(BundleEntry::getGood).collect(Collectors.toSet());
        Map<Good, Integer> map = new LinkedHashMap<>();
        for (Good good : goods) {
            Set<BundleEntry> first = getBundleEntries().stream().filter(entry -> entry.getGood().equals(good)).collect(Collectors.toSet());
            Set<BundleEntry> second = other.getBundleEntries().stream().filter(entry -> entry.getGood().equals(good)).collect(Collectors.toSet());
            int total = first.stream().mapToInt(BundleEntry::getAmount).sum() + second.stream().mapToInt(BundleEntry::getAmount).sum();
            if (cutoff) {
                map.put(good, Math.min(total, good.getQuantity()));
            } else {
                map.put(good, total);
            }
        }
        return new Bundle(map);
    }

    /**
     * Checks if a good is contained in this bundle
     *
     * @param good the good
     * @return true if the good is contained in this bundle; else false
     */
    public boolean contains(Good good) {
        return bundleEntries.stream().anyMatch(be -> be.getGood().equals(good));
    }

    /**
     * Gets many times a good is contained in the bundle.
     *
     * @param good the good
     * @return the int
     */
    public int countGood(Good good) {
        return contains(good) ? bundleEntries.stream().filter(be -> be.getGood().equals(good)).mapToInt(BundleEntry::getAmount).sum() : 0;
    }

    /**
     * Gets total amount of goods in this bundle, accounting for the quantities of the goods.
     *
     * @return the total amount
     */
    public int getTotalAmount() {
        return bundleEntries.stream().mapToInt(BundleEntry::getAmount).sum();
    }

    /**
     * The overridden toString() method distinguishes between the case where all goods are single quantity goods and
     * the case where at least one good is a multi-quantity good.
     *
     * @return the string notation of this bundle
     */
    @Override
    public String toString() {
        if (areSingleQuantityGoods()) {
            List<? extends Good> goods = getSingleQuantityGoods();
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
