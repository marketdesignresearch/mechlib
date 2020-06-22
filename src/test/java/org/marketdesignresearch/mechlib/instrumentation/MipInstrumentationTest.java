package org.marketdesignresearch.mechlib.instrumentation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.data.Offset;
import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.BundleEntry;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.SimpleGood;
import org.marketdesignresearch.mechlib.core.SimpleORDomain;
import org.marketdesignresearch.mechlib.core.SimpleXORDomain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.core.bidder.ORBidder;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.BundleValue;
import org.marketdesignresearch.mechlib.core.bidder.valuefunction.ORValueFunction;
import org.marketdesignresearch.mechlib.core.price.LinearPrices;
import org.marketdesignresearch.mechlib.core.price.Price;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.input.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.input.cats.CATSAuction;
import org.marketdesignresearch.mechlib.input.cats.CATSParser;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.CCAuction;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate.PriceUpdater;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.priceupdate.SimpleRelativePriceUpdate;
import org.marketdesignresearch.mechlib.mechanism.auctions.cca.supplementaryphase.ProfitMaximizingSupplementaryPhase;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRuleGenerator;
import org.marketdesignresearch.mechlib.outcomerules.vcg.ORVCGRule;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MipInstrumentationTest {

    @Test
    public void testVCGInstrumentation() throws IOException {

        Path catsFile = Paths.get("src/test/resources/0000.txt");
        CATSParser parser = new CATSParser();
        CATSAuction catsAuction = parser.readCatsAuctionBean(catsFile);
        CATSAdapter adapter = new CATSAdapter();
        BundleExactValueBids bids = adapter.adaptCATSAuction(catsAuction);
        OutcomeRule vcgRule = new ORVCGRule(bids);
        vcgRule.setMipInstrumentation(new MipLoggingInstrumentation());
        vcgRule.getOutcome();
    }

    @Test
    public void testBidderDemandQueryInstrumentation() {
        SimpleGood A = new SimpleGood("A", 2, false); // 10 - 8 = 2
        SimpleGood B = new SimpleGood("B", 2, false); // 11 - 7 = 4
        SimpleGood C = new SimpleGood("C", 2, false); // 12 - 7 = 5
        SimpleGood D = new SimpleGood("D", 2, false); // 13 - 35 = -22
        Map<Good, Price> priceMap = new HashMap<>();
        priceMap.put(A, Price.of(8));
        priceMap.put(B, Price.of(7));
        priceMap.put(C, Price.of(7));
        priceMap.put(D, Price.of(35));
        Prices prices = new LinearPrices(priceMap);

        Set<BundleValue> value = new HashSet<>();
        value.add(new BundleValue(BigDecimal.valueOf(10),
                new Bundle(Sets.newHashSet(new BundleEntry(A, 1)))));
        value.add(new BundleValue(BigDecimal.valueOf(11),
                new Bundle(Sets.newHashSet(new BundleEntry(B, 1)))));
        value.add(new BundleValue(BigDecimal.valueOf(12),
                new Bundle(Sets.newHashSet(new BundleEntry(C, 1)))));
        value.add(new BundleValue(BigDecimal.valueOf(9),
                new Bundle(Sets.newHashSet(new BundleEntry(D, 1)))));
        value.add(new BundleValue(BigDecimal.valueOf(13),
                new Bundle(Sets.newHashSet(new BundleEntry(D, 1)))));
        Bidder bidder = new ORBidder("bidder", new ORValueFunction(value));
        bidder.setMipInstrumentation(new MipLoggingInstrumentation());
        Bundle bestBundle = bidder.getBestBundle(prices);
        Set<Bundle> bestBundles = bidder.getBestBundles(prices, 10);
    }

    @Test
    public void testSimpleCATSAuctionMIPInstrumentation() throws IOException {
        Path catsFile = Paths.get("src/test/resources/hard0000.txt");
        CATSParser parser = new CATSParser();
        CATSAuction catsAuction = parser.readCatsAuctionBean(catsFile);
        CATSAdapter adapter = new CATSAdapter();
        SimpleXORDomain domain = adapter.adaptToDomain(catsAuction);

        CCAuction cca = new CCAuction(domain, OutcomeRuleGenerator.VCG_XOR, false);
        cca.setMipInstrumentation(new MipLoggingInstrumentation());
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryPhase().withNumberOfSupplementaryBids(3));
        Outcome outcome = cca.getOutcome();
        assertThat(outcome.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(8240.2519, Offset.offset(1e-4));
        log.info(outcome.toString());

    }

    @Test
    public void testSimpleCustomAuctionMIPInstrumentation() {
        SimpleGood goodA = new SimpleGood("A");
        SimpleGood goodB = new SimpleGood("B");
        Bundle A = Bundle.of(Sets.newHashSet(goodA));
        Bundle B = Bundle.of(Sets.newHashSet(goodB));
        Bundle AB = Bundle.of(Sets.newHashSet(goodA, goodB));

        Set<BundleValue> value1 = new HashSet<>();
        value1.add(new BundleValue(BigDecimal.valueOf(28), A));
        value1.add(new BundleValue(BigDecimal.valueOf(2), B));
        Set<BundleValue> value2 = new HashSet<>();
        value2.add(new BundleValue(BigDecimal.valueOf(2), A));
        value2.add(new BundleValue(BigDecimal.valueOf(28), B));
        Set<BundleValue> value3 = new HashSet<>();
        value3.add(new BundleValue(BigDecimal.valueOf(10), A));
        value3.add(new BundleValue(BigDecimal.valueOf(2), B));
        ORBidder bidder1 = new ORBidder("1", new ORValueFunction(value1));
        ORBidder bidder2 = new ORBidder("2", new ORValueFunction(value2));
        ORBidder bidder3 = new ORBidder("3", new ORValueFunction(value3));
        Domain domain = new SimpleORDomain(Lists.newArrayList(bidder1, bidder2, bidder3), Lists.newArrayList(goodA, goodB));

        CCAuction cca = new CCAuction(domain, OutcomeRuleGenerator.VCG_XOR, false);
        cca.setMipInstrumentation(new MipLoggingInstrumentation());
        cca.setPriceUpdater(new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.ONE).withPriceUpdate(BigDecimal.valueOf(2)));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryPhase().withNumberOfSupplementaryBids(3));
        Outcome outcome = cca.getOutcome();
        assertThat(outcome.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(56.0, Offset.offset(1e-4));
        log.info(outcome.toString());

    }


}
