package org.marketdesignresearch.mechlib.mechanism.auctions.cca;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleBoundValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.price.LinearPrices;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.mechanism.auctions.Auction;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionPhase;
import org.marketdesignresearch.mechlib.mechanism.auctions.AuctionRoundBuilder;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate.PriceUpdater;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate.SimpleRelativePriceUpdate;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.impl.DefaultDemandQueryInteraction;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(onConstructor = @__({@PersistenceConstructor}))
public class CCAClockPhase implements AuctionPhase<BundleExactValueBids> {

	@Setter
	private PriceUpdater priceUpdater = new SimpleRelativePriceUpdate();

	private final Prices initialPrices;

	public CCAClockPhase(Domain domain, boolean proposeStartingPrices) {
		if (proposeStartingPrices) {
			this.initialPrices = domain.proposeStartingPrices();
		} else {
			this.initialPrices = new LinearPrices(domain.getGoods());
		}
	}

	@Override
	public AuctionRoundBuilder<BundleExactValueBids> createNextRoundBuilder(Auction<BundleExactValueBids> auction) {
		Prices newPrices = this.getPrices(auction);
		return new CCAClockRoundBuilder(Collections.unmodifiableMap(auction.getDomain().getBidders().stream().collect(
				Collectors.toMap(b -> b.getId(), b -> new DefaultDemandQueryInteraction(b.getId(), newPrices, auction),(e1, e2) -> e1,LinkedHashMap::new))), auction);
	}

	private Prices getPrices(Auction<BundleExactValueBids> auction) {
		if (auction.getNumberOfRounds() == 0) {
			return this.initialPrices;
		} else {
			CCAClockRound previousRound = (CCAClockRound) auction.getLastRound();
			return priceUpdater.updatePrices(previousRound.getPrices(), auction.getDomain().getGoods().stream()
					.collect(Collectors.toMap(g -> g, g -> previousRound.getDemandBids().getDemand(g), (e1,e2)->e1,LinkedHashMap::new)));
		}
	}

	@Override
	public boolean phaseFinished(Auction<BundleExactValueBids> auction) {
		if(auction.getNumberOfRounds() == 0) {
			return false;
		} else {
			CCAClockRound previousRound = (CCAClockRound) auction.getLastRound();
			return previousRound.getOverDemand().values().stream().map(i -> i <= 0).reduce(Boolean::logicalAnd).orElse(true);
		}
	}

	@Override
	public String getType() {
		return "CLOCK";
	}

}
