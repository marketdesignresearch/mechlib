package org.marketdesignresearch.mechlib.mechanism.auctions;

import com.google.common.base.Preconditions;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.instrumentation.AuctionInstrumentation;
import org.marketdesignresearch.mechlib.mechanism.Mechanism;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.BundleValuePairTransformable;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.Interaction;
import org.marketdesignresearch.mechlib.instrumentation.AuctionInstrumentationable;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.*;

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
@Slf4j
public class Auction<T extends BundleValuePair> extends Mechanism implements AuctionInstrumentationable {

    private static int DEFAULT_MAX_BIDS = 100;
    private static int DEFAULT_MANUAL_BIDS = 0;
    private static int DEFAULT_MAX_ROUNDS = 1;

    @Getter
    private final Domain domain;
    @Getter
    private final OutcomeRuleGenerator outcomeRuleGenerator;

    @Getter @Setter
    private int maxBids = DEFAULT_MAX_BIDS;
    @Getter @Setter
    private int manualBids = DEFAULT_MANUAL_BIDS;
    @Getter @Setter
    private int maxRounds = DEFAULT_MAX_ROUNDS;
    @Getter @Setter
    private double relativeDemandQueryTolerance = 0;
    @Getter @Setter
    private double absoluteDemandQueryTolerance = 0;
    @Getter @Setter
    private double demandQueryTimeLimit = -1;

    protected List<AuctionRound<T>> rounds = new ArrayList<>();

    protected AuctionRoundBuilder<T> current;
    
    @Getter(AccessLevel.PROTECTED)
    private Map<UUID,BundleValuePairTransformable<Bid, T>> currentInteractions;

    @PersistenceConstructor
    public Auction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
        super();
        this.domain = domain;
        this.outcomeRuleGenerator = outcomeRuleGenerator;
        current = new AuctionRoundBuilder<>(outcomeRuleGenerator);
        current.setMipInstrumentation(getMipInstrumentation());
    }
    
    protected void setCurrentInteractions(Map<UUID,BundleValuePairTransformable<Bid, T>> newInteractions) {
    	this.currentInteractions = newInteractions;
    	newInteractions.forEach((b,i) -> i.setAuction(this));
    }
    
    public Interaction<Bid> getCurrentInteraction(Bidder b) {
    	return this.currentInteractions.get(b.getId());
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
        collectBids();
        closeRound();
    }

    public void collectBids() {
    	BundleValueBids<T> bids = new BundleValueBids<>();
    	this.currentInteractions.forEach((u,i) -> bids.setBid(this.getBidder(u), i.getBundleValueTransformedBid()));
    	this.current.setBids(bids);
    }
    
    /**
     *  Proposes a reasonable (currently truthful) bid for this bidder
     */
    /*
    public BundleValueBid<T> proposeBid(Bidder bidder) {
        BundleValueBid<T> bid = new BundleValueBid<T>();
        if (allowedNumberOfBids() > 0) {
            List<Bundle> bundlesToBidOn;
            int numberOfBids = Math.max(allowedNumberOfBids() - getManualBids(), 1); // Propose at least one bid
            if (restrictedBids().get(bidder) == null) {
                bundlesToBidOn = bidder.getBestBundles(getCurrentPrices(), numberOfBids, true, relativeDemandQueryTolerance, absoluteDemandQueryTolerance, demandQueryTimeLimit);
            } else {
                bundlesToBidOn = restrictedBids().get(bidder);
            }
            bundlesToBidOn.forEach(bundle -> bid.addBundleBid(new BundleValuePair(bidder.getValue(bundle), bundle, UUID.randomUUID().toString())));
        }
        return bid;
    }
    */

    /**
     * For all the bidders that did not submit a bid yet, this method collects proposed and submits a bid
     */
    /*
    public void proposeBids() {
        List<Bidder> biddersToQuery = getDomain().getBidders().stream().filter(b -> !current.getBids().getBidders().contains(b)).collect(Collectors.toList());
        for (Bidder bidder : biddersToQuery) {
            submitBid(bidder, proposeBid(bidder));
        }
    }
    */

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
        BundleValueBids<T> bids = current.getBids();
        Preconditions.checkArgument(domain.getBidders().containsAll(bids.getBidders()));
        Preconditions.checkArgument(domain.getGoods().containsAll(bids.getGoods()));
        int roundNumber = rounds.size() + 1;
        DefaultAuctionRound<T> round = new DefaultAuctionRound<>(roundNumber, bids, getCurrentPrices());
        // if (current.hasOutcome()) { TODO: This would not work if also previous rounds' bids are considered in an outcome
        //     round.setOutcome(current.getOutcome());
        // }
        getAuctionInstrumentation().postRound(round);
        rounds.add(round);
        current = new AuctionRoundBuilder<T>(outcomeRuleGenerator);
        current.setMipInstrumentation(getMipInstrumentation());
    }

    /*
    public void resetBid(Bidder bidder) {
        current.setBid(bidder, new BundleValueBid());
    }
    */
    
    // TODO 
    public void addRound(BundleValueBids<T> bids) {
        current.setBids(bids);
        closeRound();
    }
    

    public Outcome getTemporaryResult() {
    	this.collectBids();
        return current.getOutcome();
    }


    public BundleValueBids<T> getAggregatedBidsAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.subList(0, round + 1).stream()
                .map(AuctionRound::getBids)
                .reduce(new BundleValueBids<T>(), BundleValueBids::join);
    }

    public BundleValueBids<T> getBidsAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.get(round).getBids();
    }

    public Prices getPricesAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.get(round).getPrices();
    }

    public BundleValueBid<T> getAggregatedBidsAt(Bidder bidder, int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.subList(0, round + 1).stream()
                .map(AuctionRound::getBids)
                .map(bids -> bids.getBid(bidder))
                .reduce(new BundleValueBid<T>(), BundleValueBid::join);
    }

    public BundleValueBid<T> getBidsAt(Bidder bidder, int round) {
        return getBidsAt(round).getBid(bidder);
    }

    public BundleValueBids<T> getLatestAggregatedBids() {
        if (rounds.size() == 0) return new BundleValueBids<T>();
        return getAggregatedBidsAt(rounds.size() - 1);
    }

    public BundleValueBids<T> getLatestBids() {
        if (rounds.size() == 0) return new BundleValueBids<T>();
        return getBidsAt(rounds.size() - 1);
    }

    public BundleValueBid<T> getLatestAggregatedBids(Bidder bidder) {
        if (rounds.size() == 0) return new BundleValueBid<T>();
        return getAggregatedBidsAt(bidder, rounds.size() - 1);
    }

    public BundleValueBid<T> getLatestBids(Bidder bidder) {
        if (rounds.size() == 0) return new BundleValueBid<T>();
        return getBidsAt(bidder, rounds.size() - 1);
    }

    public AuctionRound<T> getRound(int index) {
        Preconditions.checkArgument(index >= 0 && index < rounds.size());
        return rounds.get(index);
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
            getRound(index).setOutcome(outcomeRuleGenerator.getOutcomeRule(getAggregatedBidsAt(index), getMipInstrumentation()).getOutcome());
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

    // region instrumentation
    @Override
    public void setMipInstrumentation(MipInstrumentation mipInstrumentation) {
        super.setMipInstrumentation(mipInstrumentation);
        this.domain.setMipInstrumentation(mipInstrumentation);
    }
    // endregion
}
