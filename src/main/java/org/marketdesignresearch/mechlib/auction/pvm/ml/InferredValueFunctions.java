package org.marketdesignresearch.mechlib.auction.pvm.ml;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;

import java.util.HashMap;
import java.util.Map;

public class InferredValueFunctions extends HashMap<Bidder, XORValueFunction> {

    public InferredValueFunctions(Map<Bidder, XORValueFunction> inferredValues) {
        super(inferredValues);
    }
}
