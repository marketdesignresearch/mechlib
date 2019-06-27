package org.marketdesignresearch.mechlib.auction;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.price.Prices;
import org.marketdesignresearch.mechlib.mechanisms.Mechanism;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.MechanismFactory;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Auction implements Mechanism {

    @Getter
    private final Domain domain;
    @Getter
    private final MechanismFactory mechanismType;
    protected List<AuctionRound> rounds = new ArrayList<>();

    protected AuctionRoundBuilder current;

    public Auction(Domain domain, MechanismFactory mechanismType) {
        this.domain = domain;
        this.mechanismType = mechanismType;
        current = new AuctionRoundBuilder(mechanismType);
    }

    public Bidder getBidder(UUID id) {
        return domain.getBidders().stream().filter(b -> b.getId().equals(id)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    public Good getGood(String id) {
        return domain.getGoods().stream().filter(b -> b.getId().equals(id)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    protected Prices getCurrentPrices() {
        return Prices.NONE;
    }

    /**
     * Per default, bidders can bid on all goods
     */
    public Map<Bidder, List<Bundle>> restrictedBids() {
        return new HashMap<>();
    }

    /**
     * Per default, bidders can bid as many bids as they'd like to
     */
    public int allowedNumberOfBids() {
        return Integer.MAX_VALUE;
    }

    public void closeRound() {
        Bids bids = current.getBids();
        Preconditions.checkArgument(domain.getBidders().containsAll(bids.getBidders()));
        Preconditions.checkArgument(domain.getGoods().containsAll(bids.getGoods()));
        int roundNumber = rounds.size() + 1;
        DefaultAuctionRound round = new DefaultAuctionRound(roundNumber, bids, getCurrentPrices());
        if (current.hasMechanismResult()) {
            round.setMechanismResult(current.getMechanismResult());
        }
        rounds.add(round);
        current = new AuctionRoundBuilder(mechanismType);
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
        Preconditions.checkArgument(restrictedBids().get(bidder) == null
                        || restrictedBids().get(bidder).containsAll(bid.getBundleBids().stream().map(BundleBid::getBundle).collect(Collectors.toSet())),
                "The bid of bidder " + bidder.getName() + " contains at least one bundle on which you are not allowed to bid!");
        Preconditions.checkArgument(bid.getBundleBids().size() <= allowedNumberOfBids(),
                "Bidder " + bidder.getName() + " tried to submit more bids than allowed. Max: " + allowedNumberOfBids());
        current.setBid(bidder, bid);
    }

    public void addRound(Bids bids) {
        submitBids(bids);
        closeRound();
    }

    public MechanismResult getTemporaryResult() {
        return current.getMechanismResult();
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
        domain.getGoods().forEach(good -> result.put(good, bids.getDemand(good) - good.available()));
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
    public MechanismResult getAuctionResultAtRound(int index) {
        if (getRound(index).getMechanismResult() == null) {
            getRound(index).setMechanismResult(mechanismType.getMechanism(getAggregatedBidsAt(index)).getMechanismResult());
        }
        return getRound(index).getMechanismResult();
    }

    /**
     * By default, the mechanism result is the last round's result, because the bids are aggregated.
     * This has to be overridden, e.g., for the sequential auction
     */
    @Override
    public MechanismResult getMechanismResult() {
        if (rounds.size() == 0) return MechanismResult.NONE;
        return getAuctionResultAtRound(rounds.size() - 1);
    }
}
