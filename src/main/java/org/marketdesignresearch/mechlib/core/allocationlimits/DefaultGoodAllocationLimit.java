package org.marketdesignresearch.mechlib.core.allocationlimits;

import java.util.Set;

import org.marketdesignresearch.mechlib.core.Good;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultGoodAllocationLimit implements GoodAllocationLimit{
	@Getter
	private final Set<? extends Good> goodAllocationLimit;
}
