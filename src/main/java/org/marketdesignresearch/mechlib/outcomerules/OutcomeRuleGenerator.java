package org.marketdesignresearch.mechlib.outcomerules;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.outcomerules.ccg.MechanismFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.VariableAlgorithmCCGFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.blockingallocation.XORBlockingCoalitionFinderFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.constraintgeneration.ConstraintGenerationAlgorithm;
import org.marketdesignresearch.mechlib.outcomerules.itemlevel.FirstPriceRule;
import org.marketdesignresearch.mechlib.outcomerules.itemlevel.SecondPriceRule;
import org.marketdesignresearch.mechlib.outcomerules.vcg.ORVCGRule;
import org.marketdesignresearch.mechlib.outcomerules.vcg.XORVCGRule;

public enum OutcomeRuleGenerator {
    VCG_OR,
    VCG_XOR,
    CCG,
    SECOND_PRICE,
    FIRST_PRICE;

    public OutcomeRule getOutcomeRule(BundleValueBids bids, MipInstrumentation mipInstrumentation) {
        OutcomeRule outcomeRule;
        switch (this) {
            case VCG_OR:
                outcomeRule = new ORVCGRule(bids);
                break;
            case VCG_XOR:
                outcomeRule = new XORVCGRule(bids);
                break;
            case CCG:
                MechanismFactory quadraticCCG = new VariableAlgorithmCCGFactory(new XORBlockingCoalitionFinderFactory(), ConstraintGenerationAlgorithm.STANDARD_CCG);
                outcomeRule = quadraticCCG.getOutcomeRule(bids);
                break;
            case SECOND_PRICE:
                outcomeRule = new SecondPriceRule(bids);
                break;
            case FIRST_PRICE:
                outcomeRule = new FirstPriceRule(bids);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        outcomeRule.setMipInstrumentation(mipInstrumentation);
        return outcomeRule;
    }

    public OutcomeRule getOutcomeRule(BundleValueBids bids) {
        return getOutcomeRule(bids, MipInstrumentation.NO_OP);
    }
}
