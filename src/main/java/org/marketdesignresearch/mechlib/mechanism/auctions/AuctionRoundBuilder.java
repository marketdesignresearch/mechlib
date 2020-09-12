package org.marketdesignresearch.mechlib.mechanism.auctions;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsExclude;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.Interaction;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The AuctionRoundBuilder is responsible to conduct a single round. I.e. it holds
 * the bidder interactions and collects bidder responses in this round. When the 
 * round is completed (i.e. this round is built {@link #build()}) the AuctionRoundBuilder
 * should collect all bids and create an AuctionRound that holds the submitted bids
 * and all other relevant state of this round.
 * 
 * @author Manuel Beyeler
 *
 * @param <BB> the bundle bid type of this AuctionRoundBuilder
 */
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({ @PersistenceConstructor }))
public abstract class AuctionRoundBuilder<BB extends BundleValueBids<?>> {

	@Getter(AccessLevel.PROTECTED)
	@Transient
	@EqualsExclude
	@ToString.Exclude
	// Circular reference
	private Auction<BB> auction;

	public AuctionRoundBuilder(Auction<BB> auction) {
		this.auction = auction;
	}

	/**
	 * May be used on persistence to handle circular references.
	 * Normally the auction will be set in the constructor.
	 * 
	 */
	void setAuction(Auction<BB> auction) {
		this.auction = auction;
		this.getInteractions().forEach((b, i) -> i.setAuction(auction));
	}

	/**
	 * @return the bidder interactions of the current round
	 */
	public abstract Map<UUID, ? extends Interaction> getInteractions();

	/**
	 * Is called when this round is over. I.e. the round should be built.
	 * @return 
	 */
	public abstract AuctionRound<BB> build();

	/**
	 * Does not close the round. Bidders are allowed to change their bids afterwards.
	 * @return collects and returns all submitted bids in this round
	 */
	public abstract BB getTemporaryBids();
}
