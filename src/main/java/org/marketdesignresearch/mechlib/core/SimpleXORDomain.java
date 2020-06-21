package org.marketdesignresearch.mechlib.core;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.input.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.input.cats.CATSAuction;
import org.marketdesignresearch.mechlib.input.cats.CATSParser;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString @EqualsAndHashCode
@RequiredArgsConstructor
public final class SimpleXORDomain implements Domain {

    @Getter
    private final List<? extends XORBidder> bidders;
    @Getter
    private final List<? extends Good> goods;

    private Allocation efficientAllocation;

    @Override
    public Allocation getEfficientAllocation() {
        if (bidders.stream().allMatch(bidder -> bidder.getValueFunction().getBundleValues().isEmpty())) {
            log.warn("Requested efficient allocation for bidders with no values attached!");
            efficientAllocation = Allocation.EMPTY_ALLOCATION;
        } else {
            if (efficientAllocation == null) {
                XORWinnerDetermination xorWDP = new XORWinnerDetermination(BundleExactValueBids.fromXORBidders(bidders));
                xorWDP.setMipInstrumentation(getMipInstrumentation());
                xorWDP.setPurpose(MipInstrumentation.MipPurpose.ALLOCATION.name());
                efficientAllocation = xorWDP.getAllocation();
            }
        }
        return efficientAllocation;
    }

    public static SimpleXORDomain fromCatsFile(Path catsFile) throws IOException {
        CATSAdapter adapter = new CATSAdapter();
        CATSAuction auction = new CATSParser().readCatsAuctionBean(catsFile);
        return adapter.adaptToDomain(auction);
    }

    @Override
    public String getName() {
        return "XOR Domain";
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
