package org.marketdesignresearch.mechlib.core.allocationlimits;

import java.util.List;

import org.marketdesignresearch.mechlib.core.Good;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultGoodAllocationLimit implements GoodAllocationLimit{
	@Getter
	private final List<? extends Good> goodAllocationLimit;
}
