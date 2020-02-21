package org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions;

import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.springframework.data.annotation.Transient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class DefaultInteraction<T extends Bid, R extends BundleValuePair> implements BundleValueTransformableInteraction<R> {
	
	@Transient
	private Auction<R> auction;
	
	private final UUID bidderUuid;
	private T submittedBid;
	
	public void submitBid(T bid) {
		this.submittedBid = bid;
	}
	
	protected T getSubmittedBid() {
		if(submittedBid == null) {
			this.submitBid(this.proposeBid());
		}
		return this.submittedBid;
	}
	
	protected abstract T proposeBid();
	
	public Bidder getBidder() {
		return auction.getBidder(this.bidderUuid);
	}
	
	public void setAuction(Auction<R> auction) {
		this.auction = auction;
	}
	
}
