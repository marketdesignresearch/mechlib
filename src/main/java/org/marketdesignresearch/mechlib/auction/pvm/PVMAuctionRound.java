package org.marketdesignresearch.mechlib.auction.pvm;

import lombok.Getter;
import org.marketdesignresearch.mechlib.auction.DefaultAuctionRound;
import org.marketdesignresearch.mechlib.auction.pvm.ml.InferredValueFunctions;
import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bidder.value.XORValue;
import org.marketdesignresearch.mechlib.domain.price.Prices;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import java.util.HashMap;
import java.util.Map;

public class PVMAuctionRound extends DefaultAuctionRound {

    @Getter
    private InferredValueFunctions inferredValues;
    @Getter
    private Allocation allocation;

    public PVMAuctionRound(int roundNumber, Bids bids, Map<Bidder, XORValue> inferredValues, Prices prices) {
        super(roundNumber, bids, prices);
        this.inferredValues = new InferredValueFunctions(inferredValues);
        Map<Bidder, Bid> currentAssumedBidMap = new HashMap<>();
        inferredValues.forEach((bidder, value) -> currentAssumedBidMap.put(bidder, value.toBid()));
        Bids currentAssumed = new Bids(currentAssumedBidMap);
        this.allocation = new XORWinnerDetermination(currentAssumed).getAllocation();
    }
}