package org.marketdesignresearch.mechlib.domain.singleitem;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.marketdesignresearch.mechlib.domain.*;

import java.util.*;

public class SingleItemAuctionInstance extends AuctionInstance {

    @Getter
    private Good item;

    @Getter
    private List<SingleItemBid> descendingHighestBids;

    public SingleItemAuctionInstance(Bids bids) {
        super(bids);
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
}
