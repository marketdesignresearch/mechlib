package org.marketdesignresearch.mechlib.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.ORBidder;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;

import java.util.List;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class SimpleORDomain implements Domain {

    @Getter
    private final List<? extends ORBidder> bidders;
    @Getter
    private final List<? extends Good> goods;

    private Allocation efficientAllocation;

    @Override
    public Allocation getEfficientAllocation() {
        if (efficientAllocation == null) {
            ORWinnerDetermination orWDP = new ORWinnerDetermination(Bids.fromORBidders(bidders));
            orWDP.setMipInstrumentation(getMipInstrumentation());
            orWDP.setPurpose(MipInstrumentation.MipPurpose.ALLOCATION);
            efficientAllocation = orWDP.getAllocation();
        }
        return efficientAllocation;
    }

    // region instrumentation
    @Getter
    private MipInstrumentation mipInstrumentation = MipInstrumentation.NO_OP;

    @Override
    public void setMipInstrumentation(MipInstrumentation mipInstrumentation) {
        this.mipInstrumentation = mipInstrumentation;
        getBidders().forEach(bidder -> bidder.setMipInstrumentation(mipInstrumentation));
    }

    // endregion

}
