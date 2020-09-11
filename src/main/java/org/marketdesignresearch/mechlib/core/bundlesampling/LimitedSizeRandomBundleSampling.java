package org.marketdesignresearch.mechlib.core.bundlesampling;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.RequiredArgsConstructor;

/**
 * This sampler returns a random bundle among all possible bundles with a size
 * below a certain limit. No information is preserved, there is no guarantee
 * that you can't get the same bundle twice.
 */
@RequiredArgsConstructor
public class LimitedSizeRandomBundleSampling implements BundleSampling {

	public LimitedSizeRandomBundleSampling(int sizeLimit) {
		this(sizeLimit, new Random());
	}

	private final int sizeLimit;
	private final Random random;

	@Override
	public Bundle getSingleBundle(List<? extends Good> goods) {
		if (goods.size() > 30) {
			throw new IllegalArgumentException("Cannot build a powerset of more than 30 goods. In future, this"
					+ " implementation could be more efficient by only creating a powerset of with bundles of <= sizeLimit.");
		}
		if (goods.stream().anyMatch(good -> good.getQuantity() > 1)) {
			throw new IllegalArgumentException("This bundle sampling currently only supports goods with quantity of 1");
		}
		Set<? extends Set<? extends Good>> powerSet = Sets.powerSet(new LinkedHashSet<>(goods));
		Set<? extends Set<? extends Good>> filteredSet = powerSet.stream().filter(set -> set.size() <= sizeLimit)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		Set<? extends Good> singleSet = Lists.newArrayList(filteredSet).get(random.nextInt(filteredSet.size()));
		return new Bundle(new LinkedHashSet<>(singleSet.stream().map(good -> new BundleEntry(good, 1))
				.collect(Collectors.toCollection(LinkedHashSet::new))));
	}
}
