package org.marketdesignresearch.mechlib.mechanism.auctions;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.instrumentation.AuctionInstrumentation;
import org.marketdesignresearch.mechlib.mechanism.Mechanism;
import org.marketdesignresearch.mechlib.instrumentation.AuctionInstrumentationable;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentationable;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Auction extends Mechanism implements AuctionInstrumentationable {

    private static int DEFAULT_MAX_BIDS = 100;
    private static int DEFAULT_MAX_ROUNDS = 1;

    @Getter
    private final Domain domain;
    @Getter
    private final OutcomeRuleGenerator outcomeRuleType;

    @Getter @Setter
    private int maxBids = DEFAULT_MAX_BIDS;
    @Getter @Setter
    private int maxRounds = DEFAULT_MAX_ROUNDS;
    @Setter
    private double relativeDemandQueryTolerance = 0;
    @Setter
    private double absoluteDemandQueryTolerance = 0;
    @Setter
    private double demandQueryTimeLimit = -1;

    protected List<AuctionRound> rounds = new ArrayList<>();

    protected AuctionRoundBuilder current;

    public Auction(Domain domain, OutcomeRuleGenerator outcomeRuleType) {
        this(domain, outcomeRuleType, new MipInstrumentation(), new AuctionInstrumentation());
    }

    public Auction(Domain domain, OutcomeRuleGenerator outcomeRuleType, MipInstrumentation mipInstrumentation) {
        this(domain, outcomeRuleType, mipInstrumentation, new AuctionInstrumentation());
    }

    public Auction(Domain domain, OutcomeRuleGenerator outcomeRuleType, AuctionInstrumentation auctionInstrumentation) {
        this(domain, outcomeRuleType, new MipInstrumentation(), auctionInstrumentation);
    }

    public Auction(Domain domain, OutcomeRuleGenerator outcomeRuleType, MipInstrumentation mipInstrumentation, AuctionInstrumentation auctionInstrumentation) {
        super(mipInstrumentation, auctionInstrumentation);
        this.domain = domain;
        this.domain.attachMipInstrumentation(getMipInstrumentation());
        this.outcomeRuleType = outcomeRuleType;
        current = new AuctionRoundBuilder(outcomeRuleType, getMipInstrumentation());
        getAuctionInstrumentation().preAuction(this);
    }

    public Bidder getBidder(UUID id) {
        return domain.getBidders().stream().filter(b -> b.getId().equals(id)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    public Good getGood(String name) {
        return domain.getGoods().stream().filter(b -> b.getName().equals(name)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    public Good getGood(UUID id) {
        return domain.getGoods().stream().filter(b -> b.getUuid().equals(id)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    public Prices getCurrentPrices() {
        return Prices.NONE;
    }

    public boolean finished() {
        return getNumberOfRounds() >= maxRounds;
    }

    public boolean currentPhaseFinished() {
        return finished();
    }

    /**
     * This fills up the not-yet-submitted bids and closes the round
     */
    public void advanceRound() {
        proposeBids();
        closeRound();
    }

    /**
     *  Proposes a reasonable (currently truthful) bid for this bidder
     */
    public Bid proposeBid(Bidder bidder) {
        Bid bid = new Bid();
        List<Bundle> bundlesToBidOn;
        if (restrictedBids().get(bidder) == null) {
            bundlesToBidOn = bidder.getBestBundles(getCurrentPrices(), allowedNumberOfBids(), true, relativeDemandQueryTolerance, absoluteDemandQueryTolerance, demandQueryTimeLimit);
        } else {
            bundlesToBidOn = restrictedBids().get(bidder);
        }
        bundlesToBidOn.forEach(bundle -> bid.addBundleBid(new BundleBid(bidder.getValue(bundle), bundle, UUID.randomUUID().toString())));
        return bid;
    }

    /**
     * For all the bidders that did not submit a bid yet, this method collects proposed and submits a bid
     */
    public void proposeBids() {
        List<Bidder> biddersToQuery = getDomain().getBidders().stream().filter(b -> !current.getBids().getBidders().contains(b)).collect(Collectors.toList());
        for (Bidder bidder : biddersToQuery) {
            submitBid(bidder, proposeBid(bidder));
        }
    }

    /**
     * Per default, bidders can bid on all goods
     */
    public Map<Bidder, List<Bundle>> restrictedBids() {
        return new HashMap<>();
    }

    /**
     * Per default, in a single item domain, only one bid is allowed.
     * In other domains, the fallback allowance is defined by the maxBids variable.
     */
    public int allowedNumberOfBids() {
        if (finished()) return 0;
        if (domain.getGoods().size() == 1) {
            return domain.getGoods().iterator().next().getQuantity();
        }
        return maxBids;
    }

    public void closeRound() {
        Bids bids = current.getBids();
        Preconditions.checkArgument(domain.getBidders().containsAll(bids.getBidders()));
        Preconditions.checkArgument(domain.getGoods().containsAll(bids.getGoods()));
        int roundNumber = rounds.size() + 1;
        DefaultAuctionRound round = new DefaultAuctionRound(roundNumber, bids, getCurrentPrices());
        // if (current.hasOutcome()) { TODO: This would not work if also previous rounds' bids are considered in an outcome
        //     round.setOutcome(current.getOutcome());
        // }
        getAuctionInstrumentation().postRound(round);
        rounds.add(round);
        current = new AuctionRoundBuilder(outcomeRuleType, getMipInstrumentation());
    }

    public void resetBid(Bidder bidder) {
        current.setBid(bidder, new Bid());
    }

    public void submitBids(Bids bids) {
        for (Bidder bidder : bids.getBidders()) {
            submitBid(bidder, bids.getBid(bidder));
        }
    }

    public void submitBid(Bidder bidder, Bid bid) {
        Preconditions.checkArgument(domain.getBidders().contains(bidder));
        if (restrictedBids().get(bidder) != null
                        && !restrictedBids().get(bidder).containsAll(bid.getBundleBids().stream().map(BundleBid::getBundle).collect(Collectors.toSet()))) {
            throw new IllegalBidException("The bid of bidder " + bidder.getName() + " contains at least one bundle on which you are not allowed to bid!");
        }
        if (bid.getBundleBids().size() > allowedNumberOfBids()) {
            throw new IllegalBidException("Bidder " + bidder.getName() + " tried to submit more bids than allowed. Max: " + allowedNumberOfBids());
        }
        current.setBid(bidder, bid);
    }

    public void addRound(Bids bids) {
        submitBids(bids);
        closeRound();
    }

    public Outcome getTemporaryResult() {
        return current.getOutcome();
    }


    public Bids getAggregatedBidsAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.subList(0, round + 1).stream()
                .map(AuctionRound::getBids)
                .reduce(new Bids(), Bids::join);
    }

    public Bids getBidsAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.get(round).getBids();
    }

    public Prices getPricesAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.get(round).getPrices();
    }

    public Bid getAggregatedBidsAt(Bidder bidder, int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.subList(0, round + 1).stream()
                .map(AuctionRound::getBids)
                .map(bids -> bids.getBid(bidder))
                .reduce(new Bid(), Bid::join);
    }

    public Bid getBidsAt(Bidder bidder, int round) {
        return getBidsAt(round).getBid(bidder);
    }

    public Bids getLatestAggregatedBids() {
        if (rounds.size() == 0) return new Bids();
        return getAggregatedBidsAt(rounds.size() - 1);
    }

    public Bids getLatestBids() {
        if (rounds.size() == 0) return new Bids();
        return getBidsAt(rounds.size() - 1);
    }

    public Bid getLatestAggregatedBids(Bidder bidder) {
        if (rounds.size() == 0) return new Bid();
        return getAggregatedBidsAt(bidder, rounds.size() - 1);
    }

    public Bid getLatestBids(Bidder bidder) {
        if (rounds.size() == 0) return new Bid();
        return getBidsAt(bidder, rounds.size() - 1);
    }

    public AuctionRound getRound(int index) {
        Preconditions.checkArgument(index >= 0 && index < rounds.size());
        return rounds.get(index);
    }

    // TODO: This counts every bid. This may not make much sense if multiple bids per round per bidder are allowed.
    public Map<Good, Integer> getOverDemandAt(int index) {
        Map<Good, Integer> result = new HashMap<>();
        Bids bids = getBidsAt(index);
        domain.getGoods().forEach(good -> result.put(good, bids.getDemand(good) - good.getQuantity()));
        return result;
    }

    public int getNumberOfRounds() {
        return rounds.size();
    }

    public void resetToRound(int index) {
        Preconditions.checkArgument(index < rounds.size());
        rounds = rounds.subList(0, index);
    }

    /**
     * By default, the bids for the auction results are aggregated in each round
     */
    public Outcome getOutcomeAtRound(int index) {
        if (getRound(index).getOutcome() == null) {
            getRound(index).setOutcome(outcomeRuleType.getOutcomeRule(getAggregatedBidsAt(index), getMipInstrumentation()).getOutcome());
        }
        return getRound(index).getOutcome();
    }

    /**
     * By default, the outcome is the last round's outcome, because the bids are aggregated.
     * This has to be overridden, e.g., for the sequential auction
     */
    @Override
    public Outcome getOutcome() {
        if (rounds.size() == 0) return Outcome.NONE;
        return getOutcomeAtRound(rounds.size() - 1);
    }
}
