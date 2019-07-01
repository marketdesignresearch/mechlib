package org.marketdesignresearch.mechlib.auction.pvm;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.marketdesignresearch.mechlib.auction.Auction;
import org.marketdesignresearch.mechlib.auction.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.auction.pvm.ml.DummyMLAlgorithm;
import org.marketdesignresearch.mechlib.auction.pvm.ml.MLAlgorithm;
import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.Bundle;
import org.marketdesignresearch.mechlib.domain.BundleBid;
import org.marketdesignresearch.mechlib.domain.Domain;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanisms.MechanismType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PVMAuction extends Auction {

    private MetaElicitation metaElicitation;

    public PVMAuction(Domain domain) {
        this(domain, MechanismType.VCG_XOR);
    }

    public PVMAuction(Domain domain, MechanismType mechanismType) {
        super(domain, mechanismType);
        Map<Bidder, MLAlgorithm> algorithms = new HashMap<>();
        for (Bidder bidder : getDomain().getBidders()) {
            algorithms.put(bidder, new DummyMLAlgorithm(bidder, getDomain().getGoods()));
        }
        metaElicitation = new MetaElicitation(algorithms);
    }

    @Override
    public void closeRound() {
        // TODO: Maybe make sure all the queried valuations came in?
        Bids bids = current.getBids();
        Preconditions.checkArgument(getDomain().getBidders().containsAll(bids.getBidders()));
        Preconditions.checkArgument(getDomain().getGoods().containsAll(bids.getGoods()));
        int roundNumber = rounds.size() + 1;
        PVMAuctionRound round = new PVMAuctionRound(roundNumber, bids, metaElicitation.process(bids), getCurrentPrices());
        rounds.add(round);
        current = new AuctionRoundBuilder(getMechanismType());
    }

    @Override
    public int allowedNumberOfBids() {
        if (rounds.size() == 0) return 5;
        else return 1;
    }

    @Override
    public boolean finished() {
        return getDomain().getBidders().stream()
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
}