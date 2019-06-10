package org.marketdesignresearch.mechlib.domain;

import com.google.common.base.Preconditions;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.domain.cats.CATSAuction;
import org.marketdesignresearch.mechlib.domain.cats.CATSParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface Domain {

    List<? extends Bidder> getBidders();
    List<? extends Good> getGoods();
    Allocation getEfficientAllocation();

    default Bidder getBidder(String name) {
        List<Bidder> bidders = getBidders().stream().filter(bidder -> bidder.getName().equals(name)).collect(Collectors.toList());
        Preconditions.checkArgument(bidders.size() == 1);
        return bidders.get(0);
    }

}
