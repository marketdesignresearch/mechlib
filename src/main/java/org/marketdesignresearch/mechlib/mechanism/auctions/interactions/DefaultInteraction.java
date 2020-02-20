package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.springframework.data.annotation.Transient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class DefaultInteraction<T extends Bid> implements Interaction<T> {
	
	@Transient
	private Auction<? extends BundleValuePair> auction;
	
	private final UUID bidderUuid;
	private T submittedBid;
	
	public void submitBid(T bid) {
		this.submittedBid = bid;
	}
	
	public T getSubmittedBid() {
		if(submittedBid == null) {
			this.submitBid(this.proposeBid());
		}
		return this.submittedBid;
	}
	
	public Bidder getBidder() {
		return auction.getBidder(this.bidderUuid);
	}
	
	public void setAuction(Auction<? extends BundleValuePair> auction) {
		this.auction = auction;
	}
	
}
