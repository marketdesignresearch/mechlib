package org.marketdesignresearch.mechlib.domain.cats;

import org.marketdesignresearch.mechlib.domain.bidder.BundleValue;
import org.marketdesignresearch.mechlib.domain.bidder.SimpleBidder;
import org.marketdesignresearch.mechlib.domain.bidder.Value;
import org.marketdesignresearch.mechlib.domain.bidder.ValueType;
import org.marketdesignresearch.mechlib.domain.AuctionInstance;
import org.marketdesignresearch.mechlib.domain.Domain;
import org.marketdesignresearch.mechlib.domain.Good;
import org.marketdesignresearch.mechlib.domain.SimpleGood;

import java.util.*;

public class CATSAdapter {

    public AuctionInstance adaptCATSAuction(CATSAuction catsAuction) {
        return adaptToDomain(catsAuction).toAuction();

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
        Map<String, Value> values = new HashMap<>();
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
                values.put(bidderId, new Value(ValueType.CATS));
            }
            values.get(bidderId).addBundleValue(bundleValue);
        }
        Set<SimpleBidder> bidders = new HashSet<>();
        values.forEach((k, v) -> bidders.add(new SimpleBidder(k, v)));
        return new Domain(bidders, new HashSet<>(goods));

    }
}
