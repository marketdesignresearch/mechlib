package org.marketdesignresearch.mechlib.core.bid;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;

import java.util.*;

public final class SingleItemBids extends Bids {

    @Getter
    private Good item;

    @Getter
    private List<SingleItemBid> descendingHighestBids;

    public SingleItemBids(Bids bids) {
        super(bids.getBidMap());
        descendingHighestBids = new ArrayList<>();
        for (Map.Entry<Bidder, Bid> bidEntry : bids.getBidMap().entrySet()) {
            SingleItemBid highestBid = null;
            for (BundleBid bundleBid : bidEntry.getValue().getBundleBids()) {
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
    public boolean setBid(Bidder bidder, Bid bid) {
        // FIXME: This should not be set anymore -> breaks sorting
        throw new UnsupportedOperationException("Not allowed");
    }
}
