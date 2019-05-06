package org.marketdesignresearch.mechlib.domain;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.mechanisms.AuctionMechanism;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.Mechanism;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Auction implements AuctionMechanism {

    @Getter
    private final Domain domain;
    @Getter
    private final Mechanism mechanism;
    private List<AuctionRound> rounds = new ArrayList<>();

    public void addRound(Bids bids) {
        Preconditions.checkArgument(domain.getBidders().containsAll(bids.getBidders()));
        Preconditions.checkArgument(domain.getGoods().containsAll(bids.getGoods()));
        AuctionRound round = new AuctionRound(rounds.size() + 1, bids, mechanism);
        rounds.add(round);
    }

    public Bids getBidsAt(int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.stream()
                .map(AuctionRound::getBids)
                .reduce(new Bids(), Bids::join);
    }

    public Bid getBidsAt(Bidder bidder, int round) {
        Preconditions.checkArgument(round >= 0 && round < rounds.size());
        return rounds.stream()
                .map(AuctionRound::getBids)
                .map(bids -> bids.getBid(bidder))
                .reduce(new Bid(), Bid::join);
    }

    public Bids getLatestBids() {
        return getBidsAt(rounds.size() - 1);
    }

    public Bid getLatestBids(Bidder bidder) {
        return getBidsAt(bidder, rounds.size() - 1);
    }

    public AuctionRound getRound(int index) {
        Preconditions.checkArgument(index >= 0 && index < rounds.size());
        return rounds.get(index);
    }

    public AuctionRound getLastRound() {
        Preconditions.checkArgument(rounds.size() > 0);
        return rounds.get(rounds.size() - 1);
    }

    public int getRounds() {
        return rounds.size();
    }

    public void resetToRound(int round) {
        Preconditions.checkArgument(round < rounds.size());
        rounds = rounds.subList(0, round);
    }

    @Override
    public AuctionResult getAuctionResult() {
        return getLastRound().getAuctionResult();
    }
}
