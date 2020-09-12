package org.marketdesignresearch.mechlib.core.bid.demand;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.price.Prices;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DemandBids extends Bids<DemandBid> {

	public DemandBids(Map<Bidder, DemandBid> bidderBidMap) {
		super(bidderBidMap);
	}

	public DemandBids() {
		super();
	}

	@Override
	protected DemandBid createEmptyBid() {
		return new DemandBid(Bundle.EMPTY);
	}

	public int getDemand(Good good) {
		int demand = 0;
		for (DemandBid bid : this.getBidMap().values()) {
			demand += bid.getDemandedBundle().countGood(good);
		}
		return demand;
	}

	public BundleExactValueBids transformToBundleValueBids(Prices prices) {
		BundleExactValueBids ret = new BundleExactValueBids();
		for (Map.Entry<Bidder, DemandBid> entry : this.getBidMap().entrySet()) {
			ret.setBid(entry.getKey(),
					new BundleExactValueBid(Set.of(
							new BundleExactValuePair(prices.getPrice(entry.getValue().getDemandedBundle()).getAmount(),
									entry.getValue().getDemandedBundle(), UUID.randomUUID().toString()))));
		}
		return ret;
	}
}
