package org.marketdesignresearch.mechlib.mechanism.auctions.cca;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.demand.DemandBids;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.DefaultPricedAuctionRound;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CCAClockRound extends DefaultPricedAuctionRound<BundleExactValueBids> {

    @Getter
    private final Map<UUID, Integer> overDemand;
    
    @Getter
    private final DemandBids demandBids;

    public CCAClockRound(Auction<BundleExactValueBids> auction, DemandBids bids, Prices prices, List<? extends Good> goods) {
        super(auction, prices);
        this.demandBids = bids;
        this.overDemand = goods.stream().collect(Collectors.toMap(Good::getUuid, good -> bids.getDemand(good) - good.getQuantity(),(e1,e2)->e1, LinkedHashMap::new));
    }
    
    @PersistenceConstructor
    protected CCAClockRound(int roundNumber, int auctionPhaseNumber, int auctionPhaseRoundNumber, Prices prices, DemandBids bids, Map<UUID, Integer> overDemand) {
        super(roundNumber, auctionPhaseNumber, auctionPhaseRoundNumber, prices);
        this.demandBids = bids;
        this.overDemand = overDemand;
    }

	@Override
    public String getDescription() {
        return "Clock Round " + getRoundNumber();
    }

    public String getType() {
        return "CLOCK";
    }

	@Override
	public BundleExactValueBids getBids() {
		return this.demandBids.transformToBundleValueBids(this.getPrices());
	}

}

