package ch.uzh.ifi.ce.domain.cats;

import ch.uzh.ifi.ce.domain.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CATSAdapter {

    public AuctionInstance adaptCATSAuction(CATSAuction catsAuction) {
        return adaptToDomain(catsAuction).toAuction();

    }

    private List<Good> adaptGoods(CATSAuction catsAuction) {
        List<Good> goods = new ArrayList<>();
        for (int i = 0; i < catsAuction.getNumberOfGoods() + catsAuction.getNumberOfDummyGoods(); ++i) {
            if (i < catsAuction.getNumberOfGoods()) {
                goods.add(new SimpleGood(false, i));
            } else {
                goods.add(new SimpleGood(true, i));
            }
        }
        return goods;
    }

    public Domain adaptToDomain(CATSAuction catsAuction) {
        List<Good> goods = adaptGoods(catsAuction);
        Values values = new Values();
        for (CATSBid catsBid : catsAuction.getCatsBids()) {
            Set<Good> goodsPerBid = new HashSet<>();
            String bidderId = "SB" + catsBid.getId();
            for (Integer id : catsBid.getGoodIds()) {
                goodsPerBid.add(goods.get(id));
                if (goods.get(id).isDummyGood()) {
                    bidderId = "DB" + id;
                }
            }
            BundleValue bundleValue = new BundleValue(catsBid.getAmount(), goodsPerBid, catsBid.getId());
            Bidder bidder = new Bidder(bidderId);
            if (!values.contains(bidderId)) {
                values.addValue(bidder, new CombinatorialValue(ValueType.CATS));
            }
            ((CombinatorialValue) values.getValue(bidder)).addBundleValue(bundleValue);
        }
        return new Domain(values, new HashSet<>(goods), catsAuction);

    }
}
