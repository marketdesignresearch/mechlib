package org.marketdesignresearch.mechlib.mechanism.auctions.pvm;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml.DummyMLAlgorithm;
import org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml.LinearRegressionMLAlgorithm;
import org.marketdesignresearch.mechlib.mechanism.auctions.pvm.ml.MLAlgorithm;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class PVMAuction extends Auction<BundleValuePair> {

    private final MetaElicitation metaElicitation;

    private final int initialBids;

    public PVMAuction(Domain domain) {
        this(domain, MLAlgorithm.Type.LINEAR_REGRESSION);
    }

    public PVMAuction(Domain domain, MLAlgorithm.Type type) {
        this(domain, type, OutcomeRuleGenerator.VCG_XOR);
    }

    public PVMAuction(Domain domain, MLAlgorithm.Type type, OutcomeRuleGenerator outcomeRuleGenerator) {
        this(domain, type, outcomeRuleGenerator, 5);
    }

    public PVMAuction(Domain domain, MLAlgorithm.Type mlType, OutcomeRuleGenerator outcomeRuleGenerator, int initialBids) {
        super(domain, outcomeRuleGenerator);
        Preconditions.checkArgument(!MLAlgorithm.Type.LINEAR_REGRESSION.equals(mlType) || initialBids > getDomain().getGoods().size(),
                "For Linear Regression, at least |goods| + 1 initial bids are needed.");
        setMaxRounds(100);
        this.initialBids = initialBids;
        Map<Bidder, MLAlgorithm> algorithms = new HashMap<>();
        for (Bidder bidder : getDomain().getBidders()) {
            switch (mlType) {
                case DUMMY:
                    algorithms.put(bidder, new DummyMLAlgorithm(bidder, getDomain().getGoods()));
                    break;
                case LINEAR_REGRESSION:
                default:
                    algorithms.put(bidder, new LinearRegressionMLAlgorithm(getDomain().getGoods()));
            }
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

    public BigDecimal getInferredValue(Bidder bidder, Bundle bundle) {
        return getInferredValue(bidder, bundle, getNumberOfRounds() - 1);
    }

    public BigDecimal getInferredValue(Bidder bidder, Bundle bundle, int roundIndex) {
        PVMAuctionRound round = (PVMAuctionRound) getRound(roundIndex);
        return round.getInferredValueFunctions().get(bidder).getValueFor(bundle);
    }

    @Override
    public void closeRound() {
        BundleValueBids<BundleValuePair> bids = current.getBids();
        Preconditions.checkArgument(getDomain().getBidders().containsAll(bids.getBidders()));
        Preconditions.checkArgument(getDomain().getGoods().containsAll(bids.getGoods()));
        Map<Bidder, List<Bundle>> requiredBids = restrictedBids();
        Map<Bidder, List<Bundle>> missingBids = new HashMap<>();
        for (Bidder bidder : getDomain().getBidders()) {
            BundleValueBid<BundleValuePair> bid = bids.getBid(bidder);
            for (Bundle bundle : requiredBids.get(bidder)) {
                if (bid.getBundleBids().stream().noneMatch(bbid -> bbid.getBundle().equals(bundle))) {
                    missingBids.putIfAbsent(bidder, new ArrayList<>());
                    missingBids.get(bidder).add(bundle);
                }
            }
        }
        Preconditions.checkArgument(missingBids.isEmpty(), "Missing reports!");
        int roundNumber = rounds.size() + 1;
        PVMAuctionRound round = new PVMAuctionRound(roundNumber, bids, getCurrentPrices(), metaElicitation.process(bids), restrictedBids());
        getAuctionInstrumentation().postRound(round);
        rounds.add(round);
        current = new AuctionRoundBuilder<BundleValuePair>(getOutcomeRuleGenerator());
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
        Map<Bidder, List<Bundle>> map = new HashMap<>();
        if (rounds.size() == 0) {
            getDomain().getBidders().forEach(bidder -> map.put(bidder, findNextBundles(bidder, initialBids)));
        } else {
            PVMAuctionRound round = (PVMAuctionRound) rounds.get(rounds.size() - 1);
            Allocation allocation = round.getInferredOptimalAllocation();
            getDomain().getBidders().forEach(bidder -> {
                Bundle allocated = allocation.allocationOf(bidder).getBundle();
                Optional<BundleValuePair> optional = getLatestAggregatedBids(bidder).getBundleBids().stream()
                        .filter(bb -> bb.getBundle().equals(allocated))
                        .findAny();
                if (!optional.isPresent()) {
                    map.put(bidder, Lists.newArrayList(allocated));
                } else {
                    Bundle next = findNextBundle(bidder);
                    if (next != null) {
                        map.put(bidder, Lists.newArrayList(next));
                    } else {
                        map.put(bidder, Lists.newArrayList()); // restrict all bids
                    }
                }
            });
        }
        return map;
    }

    private Bundle findNextBundle(Bidder bidder) {
        List<Bundle> result = findNextBundles(bidder, 1);
        if (result.isEmpty()) return null;
        return result.get(0);
    }

    private List<Bundle> findNextBundles(Bidder bidder, int amount) {
        List<Bundle> bundles = new ArrayList<>();
        BundleValueBid<BundleValuePair> currentBid = getLatestAggregatedBids(bidder);
        // First make sure that the single goods are queried -> this avoids singular matrix in linear regression
        for (Good good : getDomain().getGoods()) {
            Bundle bundle = Bundle.of(good);
            if (currentBid.getBundleBids().stream().noneMatch(bb -> bb.getBundle().equals(bundle) )) {
                bundles.add(bundle);
            }
            if (bundles.size() >= amount) {
                return bundles;
            }
        }
        for (Set<? extends Good> combination : Sets.powerSet(new LinkedHashSet<>(getDomain().getGoods()))) {
            if (combination.isEmpty()) continue;
            Bundle bundle = Bundle.of(combination);
            if (currentBid.getBundleBids().stream().noneMatch(bb -> bb.getBundle().equals(bundle)) && !bundles.contains(bundle)) {
                bundles.add(bundle);
            }
            if (bundles.size() >= amount) {
                return bundles;
            }
        }
        return bundles;
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