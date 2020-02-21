package org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions;

import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bid.demand.DemandBid;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.DemandQueryStrategy;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.DemandQuery;
import org.springframework.data.annotation.PersistenceConstructor;

public class DefaultDemandQueryInteraction extends DefaultInteraction<DemandBid, BundleValuePair> implements DemandQuery  {

	private final Prices prices;

	@PersistenceConstructor
	public DefaultDemandQueryInteraction(UUID bidder, Prices prices) {
		super(bidder);
		this.prices = prices;
	}
	
	@Override
	public DemandBid proposeBid() {
		return this.proposeDemandBid();
	}

	@Override
	public void submitBid(DemandBid bid) {
		// TODO check activity rules here
		super.submitBid(bid);
	}

	@Override
	public Prices getPrices() {
		return this.prices;
	}

	public BundleValueBid<BundleValuePair> getTransformedBid() {
		DemandBid bid = this.getSubmittedBid();
		return new BundleValueBid<>(
				Set.of(new BundleValuePair(this.prices.getPrice(bid.getDemandedBundle()).getAmount(),
						bid.getDemandedBundle(), UUID.randomUUID().toString())));
	}

	@Override
	public DemandBid proposeDemandBid() {
		return this.getBidder().getStrategy(DemandQueryStrategy.class).applyDemandStrategy(this);
	}

	@Override
	public void submitDemandBid(DemandBid bid) {
		super.submitBid(bid);
	}
}
