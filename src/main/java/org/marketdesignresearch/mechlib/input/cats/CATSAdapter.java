package org.marketdesignresearch.mechlib.input.cats;

import org.marketdesignresearch.mechlib.core.*;
import org.marketdesignresearch.mechlib.core.bid.Bids;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;

import java.util.*;

public class CATSAdapter {

    public Bids adaptCATSAuction(CATSAuction catsAuction) {
        SimpleXORDomain domain = adaptToDomain(catsAuction);
        return Bids.fromXORBidders(domain.getBidders());

    }

    private List<SimpleGood> adaptGoods(CATSAuction catsAuction) {
        List<SimpleGood> goods = new ArrayList<>();
        for (int i = 0; i < catsAuction.getNumberOfGoods() + catsAuction.getNumberOfDummyGoods(); ++i) {
            if (i < catsAuction.getNumberOfGoods()) {
                goods.add(new SimpleGood(String.valueOf(i), false));
            } else {
                goods.add(new SimpleGood(String.valueOf(i), true));
            }
        }
        return goods;
    }

    public SimpleXORDomain adaptToDomain(CATSAuction catsAuction) {
        List<SimpleGood> goods = adaptGoods(catsAuction);
        Map<String, Set<BundleValue>> values = new HashMap<>();
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
                values.put(bidderId, new HashSet<>());
            }
            values.get(bidderId).add(bundleValue);
        }
        List<XORBidder> bidders = new ArrayList<>();
        values.forEach((k, v) -> bidders.add(new XORBidder(k, new XORValueFunction(v))));
        return new SimpleXORDomain(bidders, new ArrayList<>(goods));

    }
}
