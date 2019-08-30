package org.marketdesignresearch.mechlib.mechanism.auctions.pvm;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml.DummyMLAlgorithm;
import org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml.MLAlgorithm;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class PVMAuction extends Auction {

    private final MetaElicitation metaElicitation;

    private final int initialBids;

    public PVMAuction(Domain domain) {
        this(domain, OutcomeRuleGenerator.VCG_XOR);
    }

    public PVMAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
        this(domain, outcomeRuleGenerator, 5);
    }

    public PVMAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, int initialBids) {
        super(domain, outcomeRuleGenerator);
        setMaxRounds(100);
        this.initialBids = initialBids;
        Map<Bidder, MLAlgorithm> algorithms = new HashMap<>();
        for (Bidder bidder : getDomain().getBidders()) {
            algorithms.put(bidder, new DummyMLAlgorithm(bidder, getDomain().getGoods()));
        }
        metaElicitation = new MetaElicitation(algorithms);
    }

    @PersistenceConstructor
    private PVMAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator, int initialBids, MetaElicitation metaElicitation) {
        super(domain, outcomeRuleGenerator);
        setMaxRounds(100);
        this.initialBids = initialBids;
        this.metaElicitation = metaElicitation;
    }

    @Override
    public void closeRound() {
        // TODO: Maybe make sure all the queried valuations came in?
        Bids bids = current.getBids();
        Preconditions.checkArgument(getDomain().getBidders().containsAll(bids.getBidders()));
        Preconditions.checkArgument(getDomain().getGoods().containsAll(bids.getGoods()));
        int roundNumber = rounds.size() + 1;
        PVMAuctionRound round = new PVMAuctionRound(roundNumber, bids, getCurrentPrices(), metaElicitation.process(bids));
        getAuctionInstrumentation().postRound(round);
        rounds.add(round);
        current = new AuctionRoundBuilder(getOutcomeRuleGenerator());
        current.setMipInstrumentation(getMipInstrumentation());
    }

    @Override
    public int allowedNumberOfBids() {
        if (rounds.size() == 0) return initialBids;
        else return 1;
    }

    @Override
    public boolean finished() {
        return super.finished() || getDomain().getBidders().stream()
                .noneMatch(bidder -> restrictedBids().get(bidder) == null || !restrictedBids().get(bidder).isEmpty());
    }

    @Override
    public Map<Bidder, List<Bundle>> restrictedBids() {
        if (rounds.size() == 0) return new HashMap<>();
        PVMAuctionRound round = (PVMAuctionRound) rounds.get(rounds.size() - 1);
        Allocation allocation = round.getAllocation();
        Map<Bidder, List<Bundle>> map = new HashMap<>();
        getDomain().getBidders().forEach(bidder -> {
            Bundle allocated = allocation.allocationOf(bidder).getBundle();
            Optional<BundleBid> optional = getLatestAggregatedBids(bidder).getBundleBids().stream()
                    .filter(bb -> bb.getBundle().equals(allocated))
                    .findAny();
            if (!optional.isPresent()) {
                map.put(bidder, Lists.newArrayList(allocated));
            } else {
                map.put(bidder, Lists.newArrayList()); // restrict all bids
            }
        });
        return map;
    }

    /**
     * This is a shortcut to finish all rounds & calculate the final result
     */
    @Override
    public Outcome getOutcome() {
        log.info("Finishing all rounds...");
        while (!finished()) {
            advanceRound();
        }
        log.info("Collected all bids. Running {} Auction to determine allocation & payments.", getOutcomeRuleGenerator());
        return getOutcomeAtRound(rounds.size() - 1);
    }
}