package org.marketdesignresearch.mechlib.auction.pvm;

import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.auction.pvm.ml.MLAlgorithm;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bidder.value.XORValue;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MetaElicitation {
    private final Map<Bidder, MLAlgorithm> algorithms;

    public Map<Bidder, XORValue> process(Bids bids) {
        Map<Bidder, XORValue> inferredValues = new HashMap<>();
        for (Map.Entry<Bidder, MLAlgorithm> entry : algorithms.entrySet()) {
            if (bids.getBid(entry.getKey()) != null) {
                entry.getValue().addReport(bids.getBid(entry.getKey()));
            }
            inferredValues.put(entry.getKey(), entry.getValue().inferValueFunction());
        }
        return inferredValues;
    }

}
