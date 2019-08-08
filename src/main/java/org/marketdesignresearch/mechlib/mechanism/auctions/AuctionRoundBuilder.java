package org.marketdesignresearch.mechlib.mechanism.auctions;

import lombok.*;
import org.marketdesignresearch.mechlib.core.bid.Bid;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.instrumentation.AuctionInstrumentation;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentationable;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class AuctionRoundBuilder implements MipInstrumentationable {
    private final OutcomeRuleGenerator outcomeRuleType;
    private final MipInstrumentation mipInstrumentation;

    @Getter
    private Bids bids = new Bids();
    private Outcome outcome;

    public void setBid(Bidder bidder, Bid bid) {
        outcome = null;
        bids.setBid(bidder, bid);
    }

    public Outcome getOutcome() {
        if (outcome == null) {
            outcome = outcomeRuleType.getOutcomeRule(bids, getMipInstrumentation()).getOutcome();
        }
        return outcome;
    }

    public boolean hasMechanismResult() {
        return outcome != null;
    }

    public void setBids(Bids bids) {
        outcome = null;
        this.bids = bids;
    }
}
