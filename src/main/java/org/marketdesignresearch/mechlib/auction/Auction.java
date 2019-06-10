package org.marketdesignresearch.mechlib.auction;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.domain.Domain;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.Mechanism;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.ccg.MechanismFactory;

import java.util.*;

@RequiredArgsConstructor
public class Auction implements Mechanism {

    @Getter
    protected final Domain domain;
    @Getter
    protected final MechanismFactory mechanismType;
    protected List<AuctionRound> rounds = new ArrayList<>();

    public Bidder getBidder(UUID id) {
        return domain.getBidders().stream().filter(b -> b.getId().equals(id)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    public Good getGood(String id) {
        return domain.getGoods().stream().filter(b -> b.getId().equals(id)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    /**
     * Per default, bidders can bid on all goods
     */
    public List<? extends Good> nextGoods() {
        return domain.getGoods();
    }

    public int addRound(Bids bids) {
        // TODO: Make sure the placed bids are only on allowed goods
        return addRound(new AuctionRound(rounds.size() + 1, bids));
    }

    public int addRound(AuctionRound round) {
        int roundNumber = rounds.size() + 1;
        Preconditions.checkArgument(round.getRoundNumber() == roundNumber);
        Preconditions.checkArgument(domain.getBidders().containsAll(round.getBids().getBidders()));
        Preconditions.checkArgument(domain.getGoods().containsAll(round.getBids().getGoods()));
        rounds.add(round);
        return roundNumber;
    }

    public Bids getAggregatedBidsAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.stream()
                .map(AuctionRound::getBids)
                .reduce(new Bids(), Bids::join);
    }

    public Bids getBidsAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.get(round).getBids();
    }

    public Bid getAggregatedBidsAt(Bidder bidder, int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.stream()
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

    public int getRounds() {
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
