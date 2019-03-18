package ch.uzh.ifi.ce.mechanisms.vcg;

import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.domain.Payment;
import ch.uzh.ifi.ce.domain.cats.CATSAdapter;
import ch.uzh.ifi.ce.domain.cats.CATSAuction;
import ch.uzh.ifi.ce.domain.cats.CATSParser;
import ch.uzh.ifi.ce.mechanisms.AuctionMechanism;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

public class VCGFromCATSTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(VCGFromCATSTest.class);

    @Test
    public void testNormalWinnerDetermination() throws IOException {
        Path catsFile = Paths.get("src/test/resources/0000.txt");
        CATSParser parser = new CATSParser();
        CATSAuction catsAuction = parser.readCatsAuctionBean(catsFile);
        CATSAdapter adapter = new CATSAdapter();
        AuctionInstance auctionInstance = adapter.adaptCATSAuction(catsAuction);
        AuctionMechanism ar = new ORVCGAuction(auctionInstance);
        // Compare to direct CPLEX result
        assertThat(ar.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(1787.8971);
        LOGGER.info(ar.getAllocation().toString());

    }

    @Test
    public void testEasyWinnerDetermination() throws IOException {
        Path catsFile = Paths.get("src/test/resources/easy0000.txt");
        CATSParser parser = new CATSParser();
        CATSAuction catsAuction = parser.readCatsAuctionBean(catsFile);
        CATSAdapter adapter = new CATSAdapter();
        AuctionInstance auctionInstance = adapter.adaptCATSAuction(catsAuction);
        AuctionMechanism ar = new ORVCGAuction(auctionInstance);
        Payment payment = ar.getPayment();
        // Compare to direct CPLEX result
        assertThat(ar.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(4514.844);
        Offset<Double> offset = Offset.offset(0.001);
        assertThat(payment.paymentOf(new Bidder("SB" + 1)).getAmount().doubleValue()).isEqualTo(798.446, offset);
        assertThat(payment.paymentOf(new Bidder("SB" + 3)).getAmount().doubleValue()).isEqualTo(907.544, offset);
        assertThat(payment.paymentOf(new Bidder("SB" + 5)).getAmount().doubleValue()).isEqualTo(1656.076, offset);
        assertThat(payment.paymentOf(new Bidder("SB" + 6)).getAmount().doubleValue()).isEqualTo(754.196, offset);
        assertThat(payment.paymentOf(new Bidder("SB" + 2)).getAmount()).isZero();
        LOGGER.info(payment.toString());

    }

    @Test
    public void testHardWinnerDetermination() throws IOException {
        Path catsFile = Paths.get("src/test/resources/hard0000.txt");
        CATSParser parser = new CATSParser();
        CATSAuction catsAuction = parser.readCatsAuctionBean(catsFile);
        CATSAdapter adapter = new CATSAdapter();
        AuctionInstance auctionInstance = adapter.adaptCATSAuction(catsAuction);
        AuctionMechanism ar = new ORVCGAuction(auctionInstance);
        // Compare to direct CPLEX result
        assertThat(ar.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(8.4188562000e003, Offset.offset(0.00001));
        LOGGER.info(ar.getAllocation().toString());
    }

    @Test
    public void testNoDummysWinnerDetermination() throws IOException {
        Path catsFile = Paths.get("src/test/resources/hardnodummys0000.txt");
        CATSParser parser = new CATSParser();
        CATSAuction catsAuction = parser.readCatsAuctionBean(catsFile);
        CATSAdapter adapter = new CATSAdapter();
        AuctionInstance auctionInstance = adapter.adaptCATSAuction(catsAuction);
        AuctionMechanism ar = new ORVCGAuction(auctionInstance);
        // Compare to direct CPLEX result
        assertThat(ar.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(3.2847555000e+004, Offset.offset(0.00001));
        LOGGER.info(ar.getAllocation().toString());
    }
}
