package org.marketdesignresearch.mechlib.mechanism.auctions.sequential;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.mechanism.auctions.ExactValueAuction;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.springframework.data.annotation.PersistenceConstructor;

/**
 * This class represents a sequential auction where for each round, there are only bids placed for a specific good
 * 
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class SequentialAuction extends ExactValueAuction {

    @PersistenceConstructor
    public SequentialAuction(Domain domain, OutcomeRuleGenerator outcomeRuleGenerator) {
        super(domain, outcomeRuleGenerator, new SequentialBidPhase());
        setMaxRounds(domain.getGoods().size());
    }

    /**
     * Overrides the default method to have outcomes only based on each round's bids
     */
    @Override
    public Outcome getOutcomeAtRound(OutcomeRuleGenerator generator, int index) {
        return generator.getOutcomeRule(getBidsAt(index), getMipInstrumentation()).getOutcome();
    }

    @Override
    public Outcome getOutcome(OutcomeRuleGenerator generator) {
        Outcome result = Outcome.NONE;
        for (int i = 0; i < getNumberOfRounds(); i++) {
            result = result.merge(getOutcomeAtRound(generator, i));
        }
        return result;
    }
}
