package org.marketdesignresearch.mechlib.mechanism.auctions;

import lombok.*;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValuePair;
import org.marketdesignresearch.mechlib.instrumentation.AuctionInstrumentation;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentationable;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class AuctionRoundBuilder<T extends BundleValuePair> implements MipInstrumentationable {
    private final OutcomeRuleGenerator outcomeRuleType;
    @Setter
    private MipInstrumentation mipInstrumentation = MipInstrumentation.NO_OP;

    @Getter
    private BundleValueBids<T> bids = new BundleValueBids<T>();
    private Outcome outcome;

    public void setBid(Bidder bidder, BundleValueBid<T> bid) {
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

    public void setBids(BundleValueBids bids) {
        outcome = null;
        this.bids = bids;
    }
}
