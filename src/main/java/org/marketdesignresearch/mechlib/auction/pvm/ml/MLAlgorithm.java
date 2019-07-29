package org.marketdesignresearch.mechlib.auction.pvm.ml;

import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;

public interface MLAlgorithm {
    void addReport(Bid report);
    XORValueFunction inferValueFunction();
}
