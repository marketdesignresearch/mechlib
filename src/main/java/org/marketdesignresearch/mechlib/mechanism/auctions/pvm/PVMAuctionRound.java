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

    @Getter
    private final InferredValueFunctions inferredValueFunctions;
    @Getter
    private final Allocation inferredOptimalAllocation;

    public PVMAuctionRound(int roundNumber, Bids bids, Prices prices, Map<Bidder, ValueFunction> inferredValueFunctions) {
        super(roundNumber, bids, prices);
        this.inferredValueFunctions = new InferredValueFunctions(inferredValueFunctions);
        this.inferredOptimalAllocation = this.inferredValueFunctions.calculateInferredOptimalAllocation();
    }

    @PersistenceConstructor
    private PVMAuctionRound(int roundNumber, Bids bids, Prices prices, InferredValueFunctions inferredValueFunctions, Allocation inferredOptimalAllocation) {
        super(roundNumber, bids, prices);
        this.inferredValueFunctions = inferredValueFunctions;
        this.inferredOptimalAllocation = inferredOptimalAllocation;
    }
}