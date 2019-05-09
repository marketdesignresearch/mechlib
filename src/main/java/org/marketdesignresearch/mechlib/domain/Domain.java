package org.marketdesignresearch.mechlib.domain;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bidder.XORBidder;
import org.marketdesignresearch.mechlib.domain.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.domain.cats.CATSAuction;
import org.marketdesignresearch.mechlib.domain.cats.CATSParser;
import org.marketdesignresearch.mechlib.strategy.Strategy;
import org.marketdesignresearch.mechlib.strategy.StrategySpace;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public final class Domain {

    @Getter
    private final Set<Bidder> bidders;
    @Getter
    private final Set<Good> goods;

    /**
     * Assuming agents play truthful
     *
     */
    public Bids toXORBidderAuction() {
        bidders.forEach(b -> Preconditions.checkArgument(b instanceof XORBidder));
        Set<XORBidder> xorBidders = new HashSet<>();
        bidders.forEach(b -> xorBidders.add((XORBidder) b));
        return Bids.fromXORBidders(xorBidders);
    }

    public Bids toXORBidderAuction(StrategySpace<?, ?> strategySpace) {
        return toXORBidderAuction(strategySpace, null, null);
    }

    public Bids toXORBidderAuction(StrategySpace<?, ?> strategySpace, XORBidder bidder, Strategy specificStrategy) {
        bidders.forEach(b -> Preconditions.checkArgument(b instanceof XORBidder));
        Set<XORBidder> xorBidders = new HashSet<>();
        bidders.forEach(b -> xorBidders.add((XORBidder) b));
        Bids bids = Bids.fromXORBidders(xorBidders, strategySpace::applyStrategyTo);
        if (bidder != null && specificStrategy != null) bids.setBid(bidder, specificStrategy.apply(bidder.getValue()));
        return bids;
    }

    public static Domain fromCatsFile(Path catsFile) throws IOException {
        CATSAdapter adapter = new CATSAdapter();
        CATSAuction auction = new CATSParser().readCatsAuctionBean(catsFile);
        return adapter.adaptToDomain(auction);
    }

}
