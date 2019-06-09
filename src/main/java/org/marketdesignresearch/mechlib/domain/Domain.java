package org.marketdesignresearch.mechlib.domain;

import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.domain.cats.CATSAuction;
import org.marketdesignresearch.mechlib.domain.cats.CATSParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Domain {

    List<? extends Bidder> getBidders();
    List<? extends Good> getGoods();
    Allocation getEfficientAllocation();

}
