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
import org.marketdesignresearch.mechlib.domain.bid.Bid;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.price.Prices;
import org.marketdesignresearch.mechlib.mechanisms.MechanismType;

import java.util.*;

public class PVMAuction extends Auction {

    private MetaElicitation metaElicitation;

    public PVMAuction(Domain domain) {
        this(domain, MechanismType.VCG_XOR);
    }

    public PVMAuction(Domain domain, MechanismType mechanismType) {
        this(domain, mechanismType, 20);
    }

    public PVMAuction(Domain domain, MechanismType mechanismType, int numberOfInitialBundles) {
        super(domain, mechanismType);
        Map<Bidder, MLAlgorithm> algorithms = new HashMap<>();
        for (Bidder bidder : getDomain().getBidders()) {
            algorithms.put(bidder, new DummyMLAlgorithm(bidder, getDomain().getGoods()));
        }
        metaElicitation = new MetaElicitation(algorithms);
        getInitialValues(numberOfInitialBundles);
    }

    private void getInitialValues(int numberOfBundles) {
        Map<Bidder, Bid> bids = new HashMap<>();
        Prices prices = getDomain().proposeStartingPrices();
        for (Bidder bidder : getDomain().getBidders()) {
            Bid bid = new Bid();
            List<Bundle> bestBundles = bidder.getBestBundles(prices, numberOfBundles);
            bestBundles.forEach(bundle -> bid.addBundleBid(new BundleBid(bidder.getValue(bundle), bundle, UUID.randomUUID().toString())));
            bids.put(bidder, bid);
        }
        addRound(new Bids(bids));
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
        if (rounds.size() == 0) return 20;
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