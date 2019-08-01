package org.marketdesignresearch.mechlib.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.core.cats.CATSAuction;
import org.marketdesignresearch.mechlib.core.cats.CATSParser;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public final class SimpleXORDomain implements Domain {

    @Getter
    private final List<? extends XORBidder> bidders;
    @Getter
    private final List<? extends Good> goods;

    private Allocation efficientAllocation;

    @Override
    public Allocation getEfficientAllocation() {
        if (efficientAllocation == null) {
            efficientAllocation = new XORWinnerDetermination(Bids.fromXORBidders(bidders)).getAllocation();
        }
        return efficientAllocation;
    }

    public static SimpleXORDomain fromCatsFile(Path catsFile) throws IOException {
        CATSAdapter adapter = new CATSAdapter();
        CATSAuction auction = new CATSParser().readCatsAuctionBean(catsFile);
        return adapter.adaptToDomain(auction);
    }

}
