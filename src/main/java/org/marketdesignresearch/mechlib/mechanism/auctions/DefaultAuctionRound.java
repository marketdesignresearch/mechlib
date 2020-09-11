package org.marketdesignresearch.mechlib.mechanism.auctions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Default Auction round that contains attributes for all mendatory auction state fields.
 * 
 * @author Manuel Beyeler
 *
 * @param <BB> The bundle bid type of this AuctionRound 
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(onConstructor = @__({ @PersistenceConstructor }))
public abstract class DefaultAuctionRound<BB extends BundleValueBids<?>> implements AuctionRound<BB> {

	@Getter
	private final int roundNumber;
	@Getter
	private final int auctionPhaseNumber;
	@Getter
	private final int auctionPhaseRoundNumber;

	/**
	 * Creates an AuctionRound and sets all mendatory state fields corresponding to 
	 * the current state of the given auction.
	 * 
	 * @param auction the Auction where this AuctionRound should belong to
	 * @see #getRoundNumber()
	 * @see #getAuctionPhaseNumber()
	 * @see #getAuctionPhaseRoundNumber()
	 */
	public DefaultAuctionRound(Auction<BB> auction) {
		this.roundNumber = auction.getNumberOfRounds() + 1;
		this.auctionPhaseNumber = auction.getCurrentPhaseNumber();
		this.auctionPhaseRoundNumber = auction.getCurrentPhaseRoundNumber();
	}
}
