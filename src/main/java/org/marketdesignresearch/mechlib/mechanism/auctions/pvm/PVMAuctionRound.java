package org.marketdesignresearch.mechlib.mechanism.auctions.pvm;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml.InferredValueFunctions;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Map;

@ToString
@EqualsAndHashCode(callSuper = true)
public class PVMAuctionRound extends DefaultAuctionRound {

    /**
     * Challenge:
     * - Have general value function based on inferred values that can be turned into bids, or anything that produces an allocation
     * -
     *
     */

    @Getter
    private final InferredValueFunctions inferredValues;
    @Getter
    private final Allocation allocation;

    public PVMAuctionRound(int roundNumber, Bids bids, Prices prices, Map<Bidder, ValueFunction> inferredValues) {
        super(roundNumber, bids, prices);
        this.inferredValues = new InferredValueFunctions(inferredValues);
        this.allocation = this.inferredValues.calculateInferredOptimalAllocation();
    }

    @PersistenceConstructor
    private PVMAuctionRound(int roundNumber, Bids bids, Prices prices, InferredValueFunctions inferredValues, Allocation allocation) {
        super(roundNumber, bids, prices);
        this.inferredValues = inferredValues;
        this.allocation = allocation;
    }
}