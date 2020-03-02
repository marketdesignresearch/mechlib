package org.marketdesignresearch.mechlib.mechanism.auctions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(onConstructor = @__({@PersistenceConstructor}))
public abstract class DefaultAuctionRound<T extends BundleValuePair> implements AuctionRound<T> {

    @Getter
    private final int roundNumber;
    @Getter
    private final int auctionPhaseNumber;
    @Getter
    private final int auctionPhaseRoundNumber;
    
    public DefaultAuctionRound(Auction<T> auction) {
    	this.roundNumber = auction.getNumberOfRounds()+1;
    	this.auctionPhaseNumber = auction.getCurrentPhaseNumber();
    	this.auctionPhaseRoundNumber = auction.getCurrentPhaseRoundNumber();
    }
}
