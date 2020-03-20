package org.marketdesignresearch.mechlib.mechanism.auctions;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(onConstructor = @__({@PersistenceConstructor}))
public abstract class DefaultAuctionRound<BB extends BundleValueBids<?>> implements AuctionRound<BB> {

    @Getter
    private final int roundNumber;
    @Getter
    private final int auctionPhaseNumber;
    @Getter
    private final int auctionPhaseRoundNumber;
    
    public DefaultAuctionRound(Auction<BB> auction) {
    	this.roundNumber = auction.getNumberOfRounds()+1;
    	this.auctionPhaseNumber = auction.getCurrentPhaseNumber();
    	this.auctionPhaseRoundNumber = auction.getCurrentPhaseRoundNumber();
    }
}
