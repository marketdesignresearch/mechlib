package org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ValueFunction;

public interface MLAlgorithm {
    void addReport(BundleValueBid<BundleValuePair> report);
    ValueFunction inferValueFunction();

    enum Type {
        DUMMY,
        LINEAR_REGRESSION
    }
}