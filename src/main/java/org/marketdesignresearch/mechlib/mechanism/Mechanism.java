package org.marketdesignresearch.mechlib.mechanism;

import lombok.*;
import org.marketdesignresearch.mechlib.instrumentation.AuctionInstrumentation;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;
import org.springframework.data.annotation.PersistenceConstructor;

@ToString @EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@PersistenceConstructor}))
public abstract class Mechanism implements OutcomeRule {
    @Getter @Setter
    private MipInstrumentation mipInstrumentation;
    @Getter @Setter
    private AuctionInstrumentation auctionInstrumentation;
}
