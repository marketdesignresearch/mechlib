package org.marketdesignresearch.mechlib.mechanism;

import lombok.Getter;
import lombok.Setter;
import org.marketdesignresearch.mechlib.instrumentation.AuctionInstrumentation;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;

public abstract class Mechanism implements OutcomeRule {

    @Getter
    private final MipInstrumentation mipInstrumentation;
    @Getter
    private final AuctionInstrumentation auctionInstrumentation;

    protected Mechanism() {
        this(new MipInstrumentation(), new AuctionInstrumentation());
    }

    protected Mechanism(MipInstrumentation mipInstrumentation) {
        this(mipInstrumentation, new AuctionInstrumentation());
    }

    protected Mechanism(AuctionInstrumentation auctionInstrumentation) {
        this(new MipInstrumentation(), auctionInstrumentation);
    }

    protected Mechanism(MipInstrumentation mipInstrumentation, AuctionInstrumentation auctionInstrumentation) {
        this.mipInstrumentation = mipInstrumentation;
        this.auctionInstrumentation = auctionInstrumentation;
    }
}
