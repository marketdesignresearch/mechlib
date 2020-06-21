package org.marketdesignresearch.mechlib.core.allocationlimits;

import java.util.List;

import org.marketdesignresearch.mechlib.core.Good;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultBundleSizeAndGoodAllocationLimit implements BundleSizeAndGoodAllocationLimit{
	@Getter
	private final int bundleSizeLimit;
	@Getter
	private final List<? extends Good> goodAllocationLimit;
}
