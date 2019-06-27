package org.marketdesignresearch.mechlib.auction.cca;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.auction.Auction;
import org.marketdesignresearch.mechlib.auction.AuctionRound;
import org.marketdesignresearch.mechlib.auction.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.auction.DefaultAuctionRound;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.ClockPhaseBidCollector;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.SupplementaryBidCollector;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.supplementaryround.ProfitMaximizingSupplementaryRound;
import org.marketdesignresearch.mechlib.auction.cca.bidcollection.supplementaryround.SupplementaryRound;
import org.marketdesignresearch.mechlib.auction.cca.priceupdate.PriceUpdater;
import org.marketdesignresearch.mechlib.auction.cca.priceupdate.SimpleRelativePriceUpdate;
import org.marketdesignresearch.mechlib.domain.Domain;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.price.LinearPrices;
import org.marketdesignresearch.mechlib.domain.price.Prices;
import org.marketdesignresearch.mechlib.mechanisms.Mechanism;
import org.marketdesignresearch.mechlib.mechanisms.MechanismResult;
import org.marketdesignresearch.mechlib.mechanisms.MechanismType;
import org.marketdesignresearch.mechlib.mechanisms.ccg.MechanismFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.marketdesignresearch.mechlib.auction.cca.CCARound.Type.CLOCK;
import static org.marketdesignresearch.mechlib.auction.cca.CCARound.Type.SUPPLEMENTARY;

@Slf4j
public class CCAuction extends Auction {

    private MechanismResult result;
    private Prices currentPrices;

    @Setter
    private PriceUpdater priceUpdater = new SimpleRelativePriceUpdate();
    private final List<SupplementaryRound> supplementaryRounds = new ArrayList<>();
    private Queue<SupplementaryRound> supplementaryRoundQueue = new LinkedList<>();

    @Setter
    private int maxRounds = 1000;
    @Getter
    private boolean clockPhaseCompleted = false;

    public CCAuction(Domain domain) {
        this(domain, MechanismType.CCG);
    }

    public CCAuction(Domain domain, MechanismType mechanismType) {
        this(domain, mechanismType, false);
    }

    public CCAuction(Domain domain, MechanismFactory mechanismType, Prices currentPrices) {
        this(domain, mechanismType, false);
        this.currentPrices = currentPrices;
    }

    public CCAuction(Domain domain, MechanismFactory mechanismType, boolean proposeStartingPrices) {
        super(domain, mechanismType);
        if (proposeStartingPrices) {
            this.currentPrices = getDomain().proposeStartingPrices();
        } else {
            this.currentPrices = new LinearPrices(getDomain().getGoods());
        }
    }

    @Override
    public int allowedNumberOfBids() {
        if (!clockPhaseCompleted) return 1;
        SupplementaryRound next = supplementaryRoundQueue.peek();
        return next == null ? 0 : next.getNumberOfSupplementaryBids();
    }

    @Override
    public Prices getCurrentPrices() {
        return currentPrices;
    }

    public void addSupplementaryRound(SupplementaryRound supplementaryRound) {
        supplementaryRounds.add(supplementaryRound);
        supplementaryRoundQueue.add(supplementaryRound);
    }

    @Override
    public MechanismResult getMechanismResult() {
        if (result == null) {
            log.info("Running all clock rounds...");
            while (!finished()) {
                nextRound();
            }
            log.info("Collected all bids. Running {} Auction to determine allocation & payments.", getMechanismType());
            Mechanism mechanism = getMechanismType().getMechanism(getLatestAggregatedBids());
            result = mechanism.getMechanismResult();
        }
        return result;
    }

    @Override
    public void closeRound() {
        Bids bids = current.getBids();
        Preconditions.checkArgument(getDomain().getBidders().containsAll(bids.getBidders()));
        Preconditions.checkArgument(getDomain().getGoods().containsAll(bids.getGoods()));
        int roundNumber = rounds.size() + 1;
        CCARound round = new CCARound(roundNumber, bids, getCurrentPrices());
        if (current.hasMechanismResult()) {
            round.setMechanismResult(current.getMechanismResult());
        }
        rounds.add(round);
        current = new AuctionRoundBuilder(getMechanismType());
        updatePrices();
    }

    @Override
    public boolean finished() {
        return supplementaryRoundQueue.isEmpty();
    }

    private void updatePrices() {
        Map<Good, Integer> demand = new HashMap<>();
        getDomain().getGoods().forEach(good -> demand.put(good, getLatestBids().getDemand(good)));
        Prices updatedPrices = priceUpdater.updatePrices(getCurrentPrices(), demand);
        if (getCurrentPrices().equals(updatedPrices) || getNumberOfRounds() >= maxRounds) {
            clockPhaseCompleted = true;
            return;
        }
        currentPrices = updatedPrices;
    }

    @Override
    public void nextRound() {
        List<Bidder> biddersToQuery = getDomain().getBidders().stream().filter(b -> !current.getBids().getBidders().contains(b)).collect(Collectors.toList());
        if (!clockPhaseCompleted) {
            ClockPhaseBidCollector collector = new ClockPhaseBidCollector(getNumberOfRounds() + 1, getCurrentPrices(), biddersToQuery);
            log.debug("Starting clock round {}...", getNumberOfRounds() + 1);
            submitBids(collector.collectBids());
        } else {
            if (supplementaryRoundQueue.isEmpty()) {
                log.warn("No supplementary round found to run");
                return;
            }
            SupplementaryRound supplementaryRound = supplementaryRoundQueue.peek();
            SupplementaryBidCollector collector = new SupplementaryBidCollector(getNumberOfRounds() + 1, biddersToQuery, supplementaryRound);
            log.debug("Starting supplementary round '{}'...", collector);
            submitBids(collector.collectBids());
            // TODO: To know at what supplementary round we are when adding the bids, we have this workaround
            //  of peek-poll. Later, it may make sense to have the current round noted somewhere (clock round 55? supplementary round 2? etc.)
            supplementaryRoundQueue.poll();
        }
        closeRound();
    }

    public boolean hasNextSupplementaryRound() {
        return !supplementaryRoundQueue.isEmpty();
    }

    @Override
    public void resetToRound(int index) {
        CCARound round = (CCARound) getRound(index);
        currentPrices = round.getPrices();
        if (SUPPLEMENTARY.equals(round.getType())) {
            CCARound previous = (CCARound) getRound(index - 1);
            Preconditions.checkState(CLOCK.equals(previous.getType()),
                    "Currently, the implementation does not allow to reset to another supplementary round than the first one.");
            clockPhaseCompleted = true;
            supplementaryRoundQueue = new LinkedList<>(supplementaryRounds);
        } else {
            clockPhaseCompleted = false;
            supplementaryRoundQueue = new LinkedList<>(supplementaryRounds);
        }
        result = null;
        super.resetToRound(index);
    }

}
