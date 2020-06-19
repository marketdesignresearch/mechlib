package org.marketdesignresearch.mechlib.mechanism.auctions.base;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.ExactValueAuction;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultExactValueQueryInteraction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents a sequential auction where for each round, there are only bids placed for a specific good
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SequentialAuction extends ExactValueAuction {

    public SequentialAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
        this(domain, outcomeRuleGenerator, new SequentialAuction.BidPhase());
    }

    @PersistenceConstructor
    public SequentialAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, AuctionPhase<BundleExactValueBids> phase) {
        super(domain, outcomeRuleGenerator, phase);
        setMaxRounds(domain.getGoods().size());
    }

    /**
     * Overrides the default method to have outcomes only based on each round's bids
     */
    @Override
    public Outcome getOutcomeAtRound(OutcomeRuleGenerator generator, int index) {
        return generator.getOutcomeRule(getBidsAt(index), getMipInstrumentation()).getOutcome();
    }

    @Override
    public Outcome getOutcome(OutcomeRuleGenerator generator) {
        Outcome result = Outcome.NONE;
        for (int i = 0; i < getNumberOfRounds(); i++) {
            result = result.merge(getOutcomeAtRound(generator, i));
        }
        return result;
    }

    @ToString
    @EqualsAndHashCode
    public static class BidPhase implements AuctionPhase<BundleExactValueBids> {

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

            return new BaseAuctionRoundBuilder(auction,
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

}
