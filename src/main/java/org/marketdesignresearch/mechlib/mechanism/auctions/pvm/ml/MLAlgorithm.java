package org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml;

import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;

public interface MLAlgorithm {
    void addReport(Bid report);
    ValueFunction inferValueFunction();

    enum Type {
        DUMMY,
        LINEAR_REGRESSION
    }
}