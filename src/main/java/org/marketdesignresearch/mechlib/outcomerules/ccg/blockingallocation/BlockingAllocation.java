package org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.marketdesignresearch.mechlib.core.Allocation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;

public class BlockingAllocation implements Iterable<Allocation> {
    private final Allocation mostBlockingAllocation;
    private final List<Allocation> otherBlockingAllocations;

    public BlockingAllocation(Allocation mostBlockingAllocation, List<Allocation> otherBlockingAllocations) {
        this.mostBlockingAllocation = mostBlockingAllocation;
        this.otherBlockingAllocations = otherBlockingAllocations;
    }

    public Allocation getMostBlockingAllocation() {
        return mostBlockingAllocation;
    }

    public List<Allocation> getOtherBlockingAllocations() {
        return otherBlockingAllocations;
    }

    public static BlockingAllocation of(Allocation allocation) {
        return new BlockingAllocation(allocation, Collections.emptyList());
    }

    @Override
    public Iterator<Allocation> iterator() {
        return Iterators.concat(ImmutableSet.of(mostBlockingAllocation).iterator(), otherBlockingAllocations.iterator());
    }

    public Stream<Allocation> stream() {
        return Stream.concat(Stream.of(mostBlockingAllocation), otherBlockingAllocations.stream());
    }
}
