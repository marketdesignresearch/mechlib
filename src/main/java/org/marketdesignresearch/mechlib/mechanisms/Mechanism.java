package org.marketdesignresearch.mechlib.mechanisms;

import org.marketdesignresearch.mechlib.domain.Bids;
import org.marketdesignresearch.mechlib.mechanisms.ccg.MechanismFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.VariableAlgorithmCCGFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.blockingallocation.XORBlockingCoalitionFinderFactory;
import org.marketdesignresearch.mechlib.mechanisms.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import org.marketdesignresearch.mechlib.mechanisms.vcg.ORVCGAuction;
import org.marketdesignresearch.mechlib.mechanisms.vcg.XORVCGAuction;

public enum Mechanism {
    VCG_OR,
    VCG_XOR,
    CCG;

    public AuctionMechanism create(Bids bids) {
        switch (this) {
            case VCG_OR:
                return new ORVCGAuction(bids);
            case VCG_XOR:
                return new XORVCGAuction(bids);
            case CCG:
                MechanismFactory quadraticCCG = new VariableAlgorithmCCGFactory(new XORBlockingCoalitionFinderFactory(), ConstraintGenerationAlgorithm.STANDARD_CCG);
                return quadraticCCG.getMechanism(bids);
            default:
                throw new UnsupportedOperationException();
        }
    }
}
