package org.marketdesignresearch.mechlib.core.allocationlimits;

import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultBundleSizeAndGoodAllocationLimit implements BundleSizeAndGoodAllocationLimit{
	@Getter
	private final int bundleSizeLimit;
	@Getter
	private final Set<? extends Good> goodAllocationLimit;
}
