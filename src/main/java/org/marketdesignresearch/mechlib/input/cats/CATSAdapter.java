package org.marketdesignresearch.mechlib.input.cats;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.SimpleGood;
import org.marketdesignresearch.mechlib.core.SimpleXORDomain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.XORValueFunction;

public class CATSAdapter {

	public BundleExactValueBids adaptCATSAuction(CATSAuction catsAuction) {
		SimpleXORDomain domain = adaptToDomain(catsAuction);
		return BundleExactValueBids.fromXORBidders(domain.getBidders());
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
		Map<String, Set<BundleValue>> values = new LinkedHashMap<>();
		for (CATSBid catsBid : catsAuction.getCatsBids()) {
			Set<Good> goodsPerBid = new LinkedHashSet<>();
			String bidderId = "SB" + catsBid.getId();
			for (Integer id : catsBid.getGoodIds()) {
				goodsPerBid.add(goods.get(id));
				if (goods.get(id).isDummyGood()) {
					bidderId = "DB" + id;
				}
			}
			BundleValue bundleValue = new BundleValue(catsBid.getAmount(), goodsPerBid,
					String.valueOf(catsBid.getId()));
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
