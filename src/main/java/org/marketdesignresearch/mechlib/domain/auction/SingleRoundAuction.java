package org.marketdesignresearch.mechlib.domain.auction;

import com.google.common.collect.Lists;
import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.domain.Good;

import java.util.Set;

public class SingleRoundAuction extends Auction {

    public SingleRoundAuction(Set<Bidder> bidders, Good item) {
        super(bidders, Lists.newArrayList(item));
    }

    public Good getItem() {
        return getGoods().get(0);
    }

}
