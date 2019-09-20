package org.marketdesignresearch.mechlib.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.ORBidder;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.winnerdetermination.ORWinnerDetermination;

import java.util.List;

@Slf4j
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
            if (bidders.stream().allMatch(bidder -> bidder.getValue().getBundleValues().isEmpty())) {
                log.warn("Requested efficient allocation for bidders with no values attached!");
                efficientAllocation = Allocation.EMPTY_ALLOCATION;
            } else {
                ORWinnerDetermination orWDP = new ORWinnerDetermination(Bids.fromORBidders(bidders));
                orWDP.setMipInstrumentation(getMipInstrumentation());
                orWDP.setPurpose(MipInstrumentation.MipPurpose.ALLOCATION);
                efficientAllocation = orWDP.getAllocation();
            }
        }
        return efficientAllocation;
    }

    @Override
    public String getName() {
        return "OR Domain";
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
