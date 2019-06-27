package org.marketdesignresearch.mechlib.auction.pvm.ml;

import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bidder.value.XORValue;

public interface MLAlgorithm {
    void addReport(Bid report);
    XORValue inferValueFunction();
}
