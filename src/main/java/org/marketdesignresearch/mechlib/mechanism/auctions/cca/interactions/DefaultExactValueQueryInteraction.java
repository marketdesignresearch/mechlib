package org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions;

import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.newstrategy.ExactValueQueryStrategy;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.ExactValueQuery;

import lombok.Getter;

public class DefaultExactValueQueryInteraction extends DefaultInteraction<BundleValueBid<BundleValuePair>, BundleValuePair> implements ExactValueQuery {

	@Getter
	private final Set<Bundle> queriedBundles;

	public DefaultExactValueQueryInteraction(Set<Bundle> bundles, UUID bidder) {
		super(bidder);
		this.queriedBundles = bundles;
	}

	@Override
	public BundleValueBid<BundleValuePair> getTransformedBid() {
		return super.getSubmittedBid();
	}

	@Override
	public BundleValueBid<BundleValuePair> proposeBid() {
		return this.proposeExactValueBids();
	}

	@Override
	public BundleValueBid<BundleValuePair> proposeExactValueBids() {
		return this.getBidder().getStrategy(ExactValueQueryStrategy.class).applyExactValueStrategy(this);
	}

	@Override
	public void submitExactValueBids(BundleValueBid<BundleValuePair> bid) {
		this.submitBid(bid);
	}

}
