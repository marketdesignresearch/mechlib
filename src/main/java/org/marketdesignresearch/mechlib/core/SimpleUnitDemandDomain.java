package org.marketdesignresearch.mechlib.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.UnitDemandBidder;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.core.cats.CATSAuction;
import org.marketdesignresearch.mechlib.core.cats.CATSParser;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class SimpleUnitDemandDomain implements Domain {

    @Getter
    private final List<? extends UnitDemandBidder> bidders;
    @Getter
    private final List<? extends Good> goods;

    private Allocation efficientAllocation;

    @Override
    public Allocation getEfficientAllocation() {
        if (efficientAllocation == null) {
            Map<UnitDemandBidder, BidderAllocation> allocationMap = new HashMap<>();
            Queue<UnitDemandBidder> bidderQueue = bidders.stream().sorted(Comparator.comparing(UnitDemandBidder::getValue)).collect(Collectors.toCollection(LinkedList::new));
            for (Good good : goods) {
                for (int i = 0; i < good.getQuantity() && !bidderQueue.isEmpty(); i++) {
                    UnitDemandBidder winner = bidderQueue.poll();
                    allocationMap.put(winner, new BidderAllocation(winner.getValue(), Bundle.of(good), new HashSet<>()));
                }
            }
            efficientAllocation = new Allocation(allocationMap, new Bids(), new MetaInfo());
        }
        return efficientAllocation;
    }

}
