package org.marketdesignresearch.mechlib.auction.pvm;

import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.auction.pvm.ml.MLAlgorithm;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MetaElicitation {
    private final Map<Bidder, MLAlgorithm> algorithms;

    public Map<Bidder, XORValueFunction> process(Bids bids) {
        Map<Bidder, XORValueFunction> inferredValues = new HashMap<>();
        for (Map.Entry<Bidder, MLAlgorithm> entry : algorithms.entrySet()) {
            if (bids.getBid(entry.getKey()) != null) {
                entry.getValue().addReport(bids.getBid(entry.getKey()));
            }
            inferredValues.put(entry.getKey(), entry.getValue().inferValueFunction());
        }
        return inferredValues;
    }

}
