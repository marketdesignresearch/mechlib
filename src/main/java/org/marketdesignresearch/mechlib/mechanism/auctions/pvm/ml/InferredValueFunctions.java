package org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.marketdesignresearch.mechlib.winnerdetermination.WinnerDetermination;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@PersistenceConstructor}))
@ToString @EqualsAndHashCode
public class InferredValueFunctions {

    @Getter
    private final Map<UUID, ValueFunction> map;
    private final Set<Bidder> bidders;

    public InferredValueFunctions(Map<Bidder, ValueFunction> inferredValues) {
        this.bidders = ImmutableSet.copyOf(inferredValues.keySet());
        this.map = ImmutableMap.copyOf(
                inferredValues.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue))
        );
    }

    public ValueFunction get(Bidder bidder) {
        return map.get(bidder.getId());
    }

    public Allocation calculateInferredOptimalAllocation() {
        WinnerDetermination wdp = null;
        for (Bidder bidder : bidders) {
            if (wdp == null) {
                wdp = get(bidder).toWDP(bidder);
            } else {
                wdp = wdp.join(get(bidder).toWDP(bidder));
            }
        }
        if (wdp != null) {
            return wdp.getAllocation();
        } else {
            return Allocation.EMPTY_ALLOCATION;
        }
    }

}
