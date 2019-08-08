package org.marketdesignresearch.mechlib.mechanism.auctions.pvm;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml.InferredValueFunctions;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.HashMap;
import java.util.Map;

@ToString
@EqualsAndHashCode(callSuper = true)
public class PVMAuctionRound extends DefaultAuctionRound {

    @Getter
    private final InferredValueFunctions inferredValues;
    @Getter
    private final Allocation allocation;

    public PVMAuctionRound(int roundNumber, Bids bids, Prices prices, Map<Bidder, XORValueFunction> inferredValues) {
        super(roundNumber, bids, prices);
        this.inferredValues = new InferredValueFunctions(inferredValues);
        Map<Bidder, Bid> currentAssumedBidMap = new HashMap<>();
        inferredValues.forEach((bidder, value) -> currentAssumedBidMap.put(bidder, value.toBid()));
        Bids currentAssumed = new Bids(currentAssumedBidMap);
        this.allocation = new XORWinnerDetermination(currentAssumed).getAllocation();
    }

    @PersistenceConstructor
    private PVMAuctionRound(int roundNumber, Bids bids, Prices prices, InferredValueFunctions inferredValues, Allocation allocation) {
        super(roundNumber, bids, prices);
        this.inferredValues = inferredValues;
        this.allocation = allocation;
    }
}