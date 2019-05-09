package org.marketdesignresearch.mechlib.mechanisms;

import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bid.SingleItemBids;
import org.marketdesignresearch.mechlib.mechanisms.ccg.MechanismFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.VariableAlgorithmCCGFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.XORBlockingCoalitionFinderFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import org.marketdesignresearch.mechlib.mechanisms.singleitem.FirstPriceAuction;
import org.marketdesignresearch.mechlib.mechanisms.singleitem.SecondPriceAuction;
import org.marketdesignresearch.mechlib.mechanisms.vcg.ORVCGAuction;
import org.marketdesignresearch.mechlib.mechanisms.vcg.XORVCGAuction;

public enum MechanismType implements MechanismFactory {
    // FIXME: Think about a better naming -> Factory? (-Pattern?)
    VCG_OR,
    VCG_XOR,
    CCG,
    SINGLE_ITEM_SECOND_PRICE,
    SINGLE_ITEM_FIRST_PRICE;

    @Override
    public AuctionMechanism getMechanism(Bids bids) {
        switch (this) {
            case VCG_OR:
                return new ORVCGAuction(bids);
            case VCG_XOR:
                return new XORVCGAuction(bids);
            case CCG:
                MechanismFactory quadraticCCG = new VariableAlgorithmCCGFactory(new XORBlockingCoalitionFinderFactory(), ConstraintGenerationAlgorithm.STANDARD_CCG);
                return quadraticCCG.getMechanism(bids);
            case SINGLE_ITEM_SECOND_PRICE:
                return new SecondPriceAuction(bids);
            case SINGLE_ITEM_FIRST_PRICE:
                return new FirstPriceAuction(bids);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public String getMechanismName() {
        return this.name();
    }
}
