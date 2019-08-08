package org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.*;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@PersistenceConstructor}))
@ToString @EqualsAndHashCode
public class InferredValueFunctions {

    @Getter
    private final Map<UUID, XORValueFunction> map;
    private final Set<Bidder> bidders;

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
