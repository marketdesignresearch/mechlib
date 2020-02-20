package org.marketdesignresearch.mechlib.core.bid.bundle;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import java.util.*;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class SingleItemBids extends BundleValueBids<BundleValuePair> {
    @Getter
    private Good item;

    @Getter
    private List<SingleItemBid> descendingHighestBids;

    public SingleItemBids(BundleValueBids<BundleValuePair> bids) {
        super(bids.getBidMap());
        descendingHighestBids = new ArrayList<>();
        for (Map.Entry<Bidder, BundleValueBid<BundleValuePair>> bidEntry : bids.getBidMap().entrySet()) {
            SingleItemBid highestBid = null;
            for (BundleValuePair bundleBid : bidEntry.getValue().getBundleBids()) {
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
    public boolean setBid(Bidder bidder, BundleValueBid<BundleValuePair> bid) {
        // FIXME: This should not be set anymore -> breaks sorting
        throw new UnsupportedOperationException("Not allowed");
    }
}
