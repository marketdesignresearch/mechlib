package org.marketdesignresearch.mechlib.auction.pvm.ml;

import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bidder.value.XORValue;

import java.util.HashMap;
import java.util.Map;

public class InferredValueFunctions extends HashMap<Bidder, XORValue> {

    public InferredValueFunctions(Map<Bidder, XORValue> inferredValues) {
        super(inferredValues);
    }
}
