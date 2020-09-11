package org.marketdesignresearch.mechlib.mechanism.auctions.interactions;

import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({ @PersistenceConstructor }))
public abstract class DefaultInteraction<T extends Bid> implements TypedInteraction<T> {

	@Transient
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Getter
	protected Auction<?> auction;

	private final UUID bidderUuid;
	private T submittedBid;

	public DefaultInteraction(UUID bidderUuid, Auction<?> auction) {
		this.bidderUuid = bidderUuid;
		this.auction = auction;
	}

	public void submitBid(T bid) {
		this.submittedBid = bid;
	}

	public T getBid() {
		return this.submittedBid;
	}

	public Bidder getBidder() {
		return auction.getBidder(this.bidderUuid);
	}

	public void setAuction(Auction<?> auction) {
		this.auction = auction;
	}

	public void submitProposedBid() {
		if (this.submittedBid == null) {
			this.submitBid(this.proposeBid());
		}
	}

}
