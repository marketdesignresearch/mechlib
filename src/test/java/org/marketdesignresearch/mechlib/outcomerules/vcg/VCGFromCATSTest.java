package org.marketdesignresearch.mechlib.outcomerules.vcg;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.SimpleXORDomain;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleValueBids;
import org.marketdesignresearch.mechlib.input.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.input.cats.CATSAuction;
import org.marketdesignresearch.mechlib.input.cats.CATSParser;
import org.marketdesignresearch.mechlib.outcomerules.OutcomeRule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VCGFromCATSTest {

    @Test
    public void testNormalWinnerDetermination() throws IOException {
        Path catsFile = Paths.get("src/test/resources/0000.txt");
        CATSParser parser = new CATSParser();
        CATSAuction catsAuction = parser.readCatsAuctionBean(catsFile);
        CATSAdapter adapter = new CATSAdapter();
        BundleValueBids<?> bids = adapter.adaptCATSAuction(catsAuction);
        OutcomeRule ar = new ORVCGRule(bids);
        // Compare to direct CPLEX result
        Assertions.assertThat(ar.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(1787.8971);
        log.info(ar.getAllocation().toString());

    }

    @Test
    public void testEasyWinnerDetermination() throws IOException {
        Path catsFile = Paths.get("src/test/resources/easy0000.txt");
        CATSParser parser = new CATSParser();
        CATSAuction catsAuction = parser.readCatsAuctionBean(catsFile);
        CATSAdapter adapter = new CATSAdapter();
        SimpleXORDomain domain = adapter.adaptToDomain(catsAuction);
        OutcomeRule ar = new ORVCGRule(BundleExactValueBids.fromXORBidders(domain.getBidders()));
        Payment payment = ar.getPayment();
        // Compare to direct CPLEX result
        Assertions.assertThat(ar.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(4514.844);
        Offset<Double> offset = Offset.offset(0.001);
        assertThat(payment.paymentOf(domain.getBidder("SB" + 1)).getAmount().doubleValue()).isEqualTo(798.446, offset);
        assertThat(payment.paymentOf(domain.getBidder("SB" + 3)).getAmount().doubleValue()).isEqualTo(907.544, offset);
        assertThat(payment.paymentOf(domain.getBidder("SB" + 5)).getAmount().doubleValue()).isEqualTo(1656.076, offset);
        assertThat(payment.paymentOf(domain.getBidder("SB" + 6)).getAmount().doubleValue()).isEqualTo(754.196, offset);
        assertThat(payment.paymentOf(domain.getBidder("SB" + 2)).getAmount()).isZero();
        log.info(payment.toString());

    }

    @Test
    public void testHardWinnerDetermination() throws IOException {
        Path catsFile = Paths.get("src/test/resources/hard0000.txt");
        CATSParser parser = new CATSParser();
        CATSAuction catsAuction = parser.readCatsAuctionBean(catsFile);
        CATSAdapter adapter = new CATSAdapter();
        BundleValueBids<?> bids = adapter.adaptCATSAuction(catsAuction);
        OutcomeRule ar = new ORVCGRule(bids);
        // Compare to direct CPLEX result
        Assertions.assertThat(ar.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(8.4188562000e003, Offset.offset(0.00001));
        log.info(ar.getAllocation().toString());
    }

    @Test
    public void testNoDummysWinnerDetermination() throws IOException {
        Path catsFile = Paths.get("src/test/resources/hardnodummys0000.txt");
        CATSParser parser = new CATSParser();
        CATSAuction catsAuction = parser.readCatsAuctionBean(catsFile);
        CATSAdapter adapter = new CATSAdapter();
        BundleValueBids<?> bids = adapter.adaptCATSAuction(catsAuction);
        OutcomeRule ar = new ORVCGRule(bids);
        // Compare to direct CPLEX result
        Assertions.assertThat(ar.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(3.2847555000e+004, Offset.offset(0.00001));
        log.info(ar.getAllocation().toString());
    }
}
