package org.marketdesignresearch.mechlib.domain.cats;

import org.marketdesignresearch.mechlib.domain.*;
import org.marketdesignresearch.mechlib.domain.bid.Bids;
import org.marketdesignresearch.mechlib.domain.bidder.Bidder;
import org.marketdesignresearch.mechlib.domain.bidder.value.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.XORBidder;
import org.marketdesignresearch.mechlib.domain.bidder.value.XORValue;

import java.util.*;

public class CATSAdapter {

    public Bids adaptCATSAuction(CATSAuction catsAuction) {
        return adaptToDomain(catsAuction).toXORBidderAuction();

    }

    private List<Good> adaptGoods(CATSAuction catsAuction) {
        List<Good> goods = new ArrayList<>();
        for (int i = 0; i < catsAuction.getNumberOfGoods() + catsAuction.getNumberOfDummyGoods(); ++i) {
            if (i < catsAuction.getNumberOfGoods()) {
                goods.add(new SimpleGood(String.valueOf(i), false));
            } else {
                goods.add(new SimpleGood(String.valueOf(i), true));
            }
        }
        return goods;
    }

    public Domain adaptToDomain(CATSAuction catsAuction) {
        List<Good> goods = adaptGoods(catsAuction);
        Map<String, XORValue> values = new HashMap<>();
        for (CATSBid catsBid : catsAuction.getCatsBids()) {
            Set<Good> goodsPerBid = new HashSet<>();
            String bidderId = "SB" + catsBid.getId();
            for (Integer id : catsBid.getGoodIds()) {
                goodsPerBid.add(goods.get(id));
                if (goods.get(id).isDummyGood()) {
                    bidderId = "DB" + id;
                }
            }
            BundleValue bundleValue = new BundleValue(catsBid.getAmount(), goodsPerBid, String.valueOf(catsBid.getId()));
            if (!values.containsKey(bidderId)) {
                values.put(bidderId, new XORValue());
            }
            values.get(bidderId).addBundleValue(bundleValue);
        }
        Set<Bidder> bidders = new HashSet<>();
        values.forEach((k, v) -> bidders.add(new XORBidder(k, v)));
        return new Domain(bidders, new HashSet<>(goods));

    }
}