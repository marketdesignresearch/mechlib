package org.marketdesignresearch.mechlib.mechanism.auctions;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsExclude;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.mechanism.auctions.interactions.Interaction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@PersistenceConstructor}))
public abstract class AuctionRoundBuilder<T extends BundleValuePair> {

	@Getter(AccessLevel.PROTECTED)
	@Transient
	@EqualsExclude
	@ToString.Exclude
	// TODO may include auction ID in toString and Equals
	private Auction<T> auction;
	
	public AuctionRoundBuilder(Auction<T> auction) {
		this.auction = auction;
	}

	void setAuction(Auction<T> auction) {
		this.auction = auction;
		this.getInteractions().forEach((b,i) -> i.setAuction(auction));
	}
	
	public abstract Map<UUID, ? extends Interaction<T>> getInteractions();
	public abstract AuctionRound<T> build();
	protected abstract Outcome computeTemporaryResult(OutcomeRuleGenerator outcomeRuleGenerator);
}
