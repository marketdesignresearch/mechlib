package org.marketdesignresearch.mechlib.mechanism.auctions.cca.bidcollection.supplementaryround;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.CCAuction;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions.BundleValueTransformableInteraction;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.interactions.DefaultProfitMaxInteraction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ProfitMaximizingSupplementaryRound implements SupplementaryRound {

    private static final int DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS = 10;

    @Setter @Getter
    private int numberOfSupplementaryBids = DEFAULT_NUMBER_OF_SUPPLEMENTARY_BIDS;
    

    public ProfitMaximizingSupplementaryRound withNumberOfSupplementaryBids(int numberOfSupplementaryBids) {
        setNumberOfSupplementaryBids(numberOfSupplementaryBids);
        return this;
    }

    @Override
    public String getDescription() {
        return "Profit Maximizing Supplementary round with " + numberOfSupplementaryBids + " bids per bidder";
    }

	@Override
	public BundleValueTransformableInteraction<BundleValuePair> getInteraction(CCAuction auction, Bidder bidder) {
		return new DefaultProfitMaxInteraction(auction.getCurrentPrices(), this.numberOfSupplementaryBids, bidder.getId());
	}

}
