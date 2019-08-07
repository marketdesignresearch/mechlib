package org.marketdesignresearch.mechlib.mechanism.auctions.pvm;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;
import org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml.MLAlgorithm;

import java.util.*;

public class MetaElicitation {
    private final Map<UUID, MLAlgorithm> algorithms = new HashMap<>();
    private final Set<Bidder> bidders = new HashSet<>();

    public MetaElicitation(Map<Bidder, MLAlgorithm> bidderMLAlgorithmMap) {
        bidders.addAll(bidderMLAlgorithmMap.keySet());
        bidderMLAlgorithmMap.forEach((k, v) -> this.algorithms.put(k.getId(), v));
    }

    public Map<Bidder, XORValueFunction> process(Bids bids) {
        Map<Bidder, XORValueFunction> inferredValues = new HashMap<>();
        for (Map.Entry<UUID, MLAlgorithm> entry : algorithms.entrySet()) {
            if (bids.getBid(getBidder(entry.getKey())) != null) {
                entry.getValue().addReport(bids.getBid(getBidder(entry.getKey())));
            }
            inferredValues.put(getBidder(entry.getKey()), entry.getValue().inferValueFunction());
        }
        return inferredValues;
    }

    private Bidder getBidder(UUID id) {
        return bidders.stream().filter(b -> b.getId().equals(id)).findAny().orElseThrow(NoSuchElementException::new);
    }

}
