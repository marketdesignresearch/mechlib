package org.marketdesignresearch.mechlib.mechanism.auctions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A default implementation for the PricedAuctionRound interface. 
 * It is based on {@link DefaultAuctionRound} and handles most 
 * mendatory attributes of an AuctionRound.
 * 
 * @author Manuel Beyeler
 *
 * @param <BB> the bundle bid type of this DefaultPricesAuctionRound
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class DefaultPricedAuctionRound<BB extends BundleValueBids<?>> extends DefaultAuctionRound<BB>
		implements PricedAuctionRound<BB> {

	@Getter
	private final Prices prices;

	@PersistenceConstructor
	protected DefaultPricedAuctionRound(int roundNumber, int auctionPhaseNumber, int auctionPhaseRoundNumber,
			Prices prices) {
		super(roundNumber, auctionPhaseNumber, auctionPhaseRoundNumber);
		this.prices = prices;
	}

	public DefaultPricedAuctionRound(Auction<BB> auction, Prices prices) {
		super(auction);
		this.prices = prices;
	}

}
