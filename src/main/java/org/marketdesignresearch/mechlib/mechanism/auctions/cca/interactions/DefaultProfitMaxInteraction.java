package org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions;

import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.ProfitMaxStrategy;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ProfitMaxQuery;

public class DefaultProfitMaxInteraction extends DefaultInteraction<BundleValueBid<BundleValuePair>, BundleValuePair> implements ProfitMaxQuery{

	private final Prices prices;
	private final int numberOfBids;
	
	public DefaultProfitMaxInteraction(Prices prices, int numberOfBids, UUID bidderUuid) {
		super(bidderUuid);
		this.prices = prices;
		this.numberOfBids = numberOfBids;
	}
	
	@Override
	public BundleValueBid<BundleValuePair> getTransformedBid() {
		return super.getSubmittedBid();
	}

	@Override
	public Prices getPrices() {
		return this.prices;
	}

	@Override
	public BundleValueBid<BundleValuePair> proposeExactValueBid() {
		return this.getBidder().getStrategy(ProfitMaxStrategy.class).applyProfitMaxStrategy(this);
	}

	@Override
	protected BundleValueBid<BundleValuePair> proposeBid() {
		return this.proposeExactValueBid();
	}

	@Override
	public void submitExactValueBid(BundleValueBid<BundleValuePair> bid) {
		super.submitBid(bid);
	}

	@Override
	public int getNumberOfBids() {
		return this.numberOfBids;
	}

}
