package org.marketdesignresearch.mechlib.mechanism.auctions.base;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.ExactValueAuction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultExactValueQueryInteraction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * This class represents a sequential auction where for each round, there are only bids placed for a specific good
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SimultaneousAuction extends ExactValueAuction {

    public SimultaneousAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
        this(domain, outcomeRuleGenerator, new SimultaneousAuction.BidPhase());
    }

    @PersistenceConstructor
    public SimultaneousAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, AuctionPhase<BundleExactValueBids> phase) {
        super(domain, outcomeRuleGenerator, phase);
        setMaxRounds(1);
    }

    @ToString
    @EqualsAndHashCode
    public static class BidPhase implements AuctionPhase<BundleExactValueBids> {
        @Override
        public AuctionRoundBuilder<BundleExactValueBids> createNextRoundBuilder(Auction<BundleExactValueBids> auction) {
            return new BaseAuctionRoundBuilder(auction,
                    auction.getDomain().getBidders().stream().collect(
                            Collectors.toMap(Bidder::getId, b -> new DefaultExactValueQueryInteraction(
                                    auction.getDomain().getGoods().stream().map(Bundle::of).collect(Collectors.toSet()),
                                    b.getId(),
                                    auction), (e1, e2) -> e1, LinkedHashMap::new)));
        }

        @Override
        public boolean phaseFinished(Auction<BundleExactValueBids> auction) {
            return auction.getNumberOfRounds() >= 1;
        }

        @Override
        public String getType() {
            return "SIMULTANEOUS PHASE";
        }
    }
}