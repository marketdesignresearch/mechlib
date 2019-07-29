package org.marketdesignresearch.mechlib.mechanisms;

import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.mechanisms.ccg.MechanismFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.VariableAlgorithmCCGFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.XORBlockingCoalitionFinderFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import org.marketdesignresearch.mechlib.mechanisms.itemlevel.FirstPriceMechanism;
import org.marketdesignresearch.mechlib.mechanisms.itemlevel.SecondPriceMechanism;
import org.marketdesignresearch.mechlib.mechanisms.vcg.ORVCGMechanism;
import org.marketdesignresearch.mechlib.mechanisms.vcg.XORVCGMechanism;

public enum MechanismType implements MechanismFactory {
    // FIXME: Think about a better naming -> Factory? (-Pattern?)
    VCG_OR,
    VCG_XOR,
    CCG,
    SECOND_PRICE,
    FIRST_PRICE;

    @Override
    public OutputRule getMechanism(Bids bids) {
        switch (this) {
            case VCG_OR:
                return new ORVCGMechanism(bids);
            case VCG_XOR:
                return new XORVCGMechanism(bids);
            case CCG:
                MechanismFactory quadraticCCG = new VariableAlgorithmCCGFactory(new XORBlockingCoalitionFinderFactory(), ConstraintGenerationAlgorithm.STANDARD_CCG);
                return quadraticCCG.getMechanism(bids);
            case SECOND_PRICE:
                return new SecondPriceMechanism(bids);
            case FIRST_PRICE:
                return new FirstPriceMechanism(bids);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public String getMechanismName() {
        return this.name();
    }
}
