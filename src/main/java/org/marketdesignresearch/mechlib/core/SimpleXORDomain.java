package org.marketdesignresearch.mechlib.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.core.cats.CATSAuction;
import org.marketdesignresearch.mechlib.core.cats.CATSParser;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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
        if (efficientAllocation == null) {
            XORWinnerDetermination xorWDP = new XORWinnerDetermination(Bids.fromXORBidders(bidders));
            xorWDP.setMipInstrumentation(getMipInstrumentation());
            xorWDP.setPurpose(MipInstrumentation.MipPurpose.ALLOCATION);
            efficientAllocation = xorWDP.getAllocation();
        }
        return efficientAllocation;
    }

    public static SimpleXORDomain fromCatsFile(Path catsFile) throws IOException {
        CATSAdapter adapter = new CATSAdapter();
        CATSAuction auction = new CATSParser().readCatsAuctionBean(catsFile);
        return adapter.adaptToDomain(auction);
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