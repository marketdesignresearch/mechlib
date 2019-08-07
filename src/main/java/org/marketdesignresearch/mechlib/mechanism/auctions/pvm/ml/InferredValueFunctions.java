package org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;

import java.util.*;
import java.util.stream.Collectors;

public class InferredValueFunctions {

    @Getter
    private final ImmutableMap<UUID, XORValueFunction> map;
    private final ImmutableSet<Bidder> bidders;

    public InferredValueFunctions(Map<Bidder, XORValueFunction> inferredValues) {
        this.bidders = ImmutableSet.copyOf(inferredValues.keySet());
        this.map = ImmutableMap.copyOf(
                inferredValues.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue))
        );
    }

    public XORValueFunction get(Bidder bidder) {
        return map.get(bidder.getId());
    }

}
