package org.marketdesignresearch.mechlib.mechanism.auctions.pvm;

import lombok.Getter;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultAuctionRound;
import org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml.InferredValueFunctions;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PVMAuctionRound extends DefaultAuctionRound {

    @Getter
    private InferredValueFunctions inferredValues;
    @Getter
    private Allocation allocation;

    public PVMAuctionRound(int roundNumber, Bids bids, Map<Bidder, XORValueFunction> inferredValues, Prices prices) {
        super(roundNumber, bids, prices);
        this.inferredValues = new InferredValueFunctions(inferredValues);
        Map<Bidder, Bid> currentAssumedBidMap = new HashMap<>();
        inferredValues.forEach((bidder, value) -> currentAssumedBidMap.put(bidder, value.toBid()));
        Bids currentAssumed = new Bids(currentAssumedBidMap);
        this.allocation = new XORWinnerDetermination(currentAssumed).getAllocation();
    }
}