package org.marketdesignresearch.mechlib.input.csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.SimpleGood;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBid;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValuePair;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.XORBidder;

import com.opencsv.bean.CsvToBeanBuilder;

public class CsvBidsReader {

    /**
     *
     * @param   path - The path to the CSV file
     * @return  The bids that are represented by this CSV file, which can be further processed
     * @throws  FileNotFoundException if there is no file at this path
     */
    public static BundleExactValueBids csvToXORBids(Path path) throws FileNotFoundException {
        // Maps the CSV rows to BidBeans
        List<BidBean> bidBeans = new CsvToBeanBuilder<BidBean>(new FileReader(path.toString()))
                .withType(BidBean.class).build().parse();
        if (bidBeans.isEmpty()) return new BundleExactValueBids();
        // Collect the information about the items
        List<SimpleGood> goods = bidBeans.get(0).getGoods().keySet().stream().map(SimpleGood::new).collect(Collectors.toList());
        Map<Bidder, BundleExactValueBid> bidMap = new HashMap<>();
        // Iterate through all BidBeans to collect the bids
        for (BidBean bidBean : bidBeans) {
            Bidder bidder = bidMap.keySet().stream().filter(b -> b.getName().equals(bidBean.getBidder())).findAny().orElse(new XORBidder(bidBean.getBidder()));
            Set<BundleEntry> bundleEntries = new HashSet<>();
            for (Good good : goods) {
                Collection<Integer> columnValues = bidBean.getGoods().get(good.getName());
                if (columnValues.size() != 1) {
                    throw new IllegalArgumentException("Bad input: " + bidBean);
                }
                int numberOfGoods = columnValues.iterator().next();
                if (numberOfGoods > 0) {
                    bundleEntries.add(new BundleEntry(good, numberOfGoods));
                }
            }
            BundleExactValuePair bundleBid = new BundleExactValuePair(bidBean.getBid(), new Bundle(bundleEntries), UUID.randomUUID().toString());
            BundleExactValueBid bid = bidMap.getOrDefault(bidder, new BundleExactValueBid());
            bid.addBundleBid(bundleBid);
            bidMap.put(bidder, bid);
        }

        return new BundleExactValueBids(bidMap);
    }

}
