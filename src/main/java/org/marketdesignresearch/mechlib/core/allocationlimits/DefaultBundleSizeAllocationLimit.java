package org.marketdesignresearch.mechlib.core.allocationlimits;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultBundleSizeAllocationLimit implements BundleSizeAllocationLimit {
	@Getter
	private final int bundleSizeLimit;
}
