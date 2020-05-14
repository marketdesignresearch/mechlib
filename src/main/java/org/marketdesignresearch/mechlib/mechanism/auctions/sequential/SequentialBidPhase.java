package org.marketdesignresearch.mechlib.mechanism.auctions.sequential;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.truthful.TruthfulExactValueQueryStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultExactValueQueryInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.simple.DefaultSimpleBidInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.simple.SimpleBidAuctionRoundBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ToString
@EqualsAndHashCode
public class SequentialBidPhase implements AuctionPhase<BundleExactValueBids> {

    @Override
    public AuctionRoundBuilder<BundleExactValueBids> createNextRoundBuilder(Auction<BundleExactValueBids> auction) {
        Map<Bidder, Bundle> alreadyWonMap = new HashMap<>();
        auction.getDomain().getBidders().forEach(b -> {
            Bundle alreadyWon = Bundle.EMPTY;
            for (int i = 0; i < auction.getNumberOfRounds(); i++) {
                alreadyWon = alreadyWon.merge(auction.getOutcomeAtRound(i).getAllocation().allocationOf(b).getBundle());
            }
            alreadyWonMap.put(b, alreadyWon);
        });

        return new SequentialAuctionRoundBuilder(auction,
                auction.getDomain().getBidders().stream().collect(
                        Collectors.toMap(Bidder::getId, b -> new DefaultExactValueQueryInteraction(
                                Set.of(Bundle.of(auction.getDomain().getGoods().get(auction.getNumberOfRounds()))),
                                b.getId(),
                                auction,
                                alreadyWonMap.get(b)), (e1, e2) -> e1, LinkedHashMap::new)));
    }

    @Override
    public boolean phaseFinished(Auction<BundleExactValueBids> auction) {
        return auction.getNumberOfRounds() >= auction.getDomain().getGoods().size();
    }

    @Override
    public String getType() {
        return "SEQUENTIAL PHASE";
    }

}
