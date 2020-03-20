package org.marketdesignresearch.mechlib.core.bid.bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class SingleItemBids extends BundleExactValueBids {
    @Getter
    private Good item;

    @Getter
    private List<SingleItemBid> descendingHighestBids;
    
    public SingleItemBids(BundleExactValueBids bids) {
        super(bids.getBidMap());
        descendingHighestBids = new ArrayList<>();
        for (Map.Entry<Bidder, BundleExactValueBid> bidEntry : bids.getBidMap().entrySet()) {
            SingleItemBid highestBid = null;
            for (BundleExactValuePair bundleBid : bidEntry.getValue().getBundleBids()) {
                Preconditions.checkArgument(bundleBid.getBundle().isSingleGood());
                Good good = bundleBid.getBundle().getSingleGood();
                if (item == null) item = good;
                Preconditions.checkArgument(item.equals(good));
                if (highestBid == null || bundleBid.getAmount().compareTo(highestBid.getBundleBid().getAmount()) > 0) {
                    highestBid = new SingleItemBid(bidEntry.getKey(), bundleBid);
                }
            }
            descendingHighestBids.add(highestBid);
        }
        descendingHighestBids.sort((a, b) -> b.getBundleBid().getAmount().compareTo(a.getBundleBid().getAmount()));
    }

    @Override
    public boolean setBid(Bidder bidder, BundleExactValueBid bid) {
        // FIXME: This should not be set anymore -> breaks sorting
        throw new UnsupportedOperationException("Not allowed");
    }
}
