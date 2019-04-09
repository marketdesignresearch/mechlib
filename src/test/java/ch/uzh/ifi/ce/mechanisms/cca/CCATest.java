package ch.uzh.ifi.ce.mechanisms.cca;

import ch.uzh.ifi.ce.demandquery.DiscreteDemandQuery;
import ch.uzh.ifi.ce.domain.Allocation;
import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.domain.cats.CATSAdapter;
import ch.uzh.ifi.ce.domain.cats.CATSAuction;
import ch.uzh.ifi.ce.domain.cats.CATSParser;
import ch.uzh.ifi.ce.domain.cats.Domain;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.cca.priceupdate.PriceUpdater;
import ch.uzh.ifi.ce.mechanisms.cca.priceupdate.SimpleRelativePriceUpdate;
import ch.uzh.ifi.ce.mechanisms.cca.round.supplementaryround.ProfitMaximizingSupplementaryRound;
import ch.uzh.ifi.ce.mechanisms.vcg.VCGAuction;
import ch.uzh.ifi.ce.mechanisms.vcg.XORVCGAuction;
import ch.uzh.ifi.ce.winnerdetermination.XORWinnerDetermination;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.data.Offset;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Slf4j
public class CCATest {

    private static Domain domain;

    @BeforeClass
    public static void setUp() throws IOException {
        Path catsFile = Paths.get("src/test/resources/hard0000.txt");
        CATSParser parser = new CATSParser();
        CATSAuction catsAuction = parser.readCatsAuctionBean(catsFile);
        CATSAdapter adapter = new CATSAdapter();
        domain = adapter.adaptToDomain(catsAuction);
    }

    @Test
    public void testCCAWithCATSAuction() {
        CCAuction cca = new CCAuction(domain.getGoods(), domain.getBidders(), new DiscreteDemandQuery(domain.getValues()));
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(3));
        AuctionResult auctionResult = cca.getAuctionResult();
        assertThat(auctionResult.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(8025.6928, Offset.offset(1e-4));
        log.info(auctionResult.toString());
    }

    @Test
    public void testRoundAfterRoundCCAWithCATSAuction() {
        VCGAuction vcgAuction = new XORVCGAuction(domain.toAuction());
        AuctionResult resultIncludingAllBids = vcgAuction.getAuctionResult();

        CCAuction cca = new CCAuction(domain.getGoods(), domain.getBidders(), new DiscreteDemandQuery(domain.getValues()));
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(2));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(3));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(4));
        Allocation previousAllocation = Allocation.EMPTY_ALLOCATION;
        while (!cca.isClockPhaseCompleted()) {
            cca.nextClockRound();
            Allocation allocation = new XORWinnerDetermination(new AuctionInstance(cca.getLatestBids())).getAllocation();
            assertThat(allocation.getTotalAllocationValue()).isLessThanOrEqualTo(resultIncludingAllBids.getAllocation().getTotalAllocationValue());
            assertThat(allocation.getTotalAllocationValue()).isGreaterThanOrEqualTo(previousAllocation.getTotalAllocationValue());
            previousAllocation = allocation;
        }
        while (cca.hasNextSupplementaryRound()) {
            cca.nextSupplementaryRound();
            Allocation allocation = new XORWinnerDetermination(new AuctionInstance(cca.getLatestBids())).getAllocation();
            assertThat(allocation.getTotalAllocationValue()).isLessThanOrEqualTo(resultIncludingAllBids.getAllocation().getTotalAllocationValue());
            assertThat(allocation.getTotalAllocationValue()).isGreaterThanOrEqualTo(previousAllocation.getTotalAllocationValue());
            previousAllocation = allocation;
        }
        AuctionResult auctionResult = cca.getAuctionResult();
        assertThat(auctionResult.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(8240.2519, Offset.offset(1e-4));
        assertThat(auctionResult.getAllocation()).isEqualTo(previousAllocation);
        log.info(auctionResult.toString());
    }

    @Test
    public void testResettingCCAWithCATSAuction() {
        CCAuction cca = new CCAuction(domain.getGoods(), domain.getBidders(), new DiscreteDemandQuery(domain.getValues()));
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(2));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(3));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(4));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(3));
        AuctionResult first = cca.getAuctionResult();
        assertThat(cca.isClockPhaseCompleted()).isTrue();
        assertThat(cca.hasNextSupplementaryRound()).isFalse();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> cca.resetToRound(50));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> cca.resetToRound(28));
        cca.resetToRound(25);
        assertThat(cca.isClockPhaseCompleted()).isFalse();
        assertThat(cca.hasNextSupplementaryRound()).isTrue();

        VCGAuction vcgAuction = new XORVCGAuction(new AuctionInstance(cca.getLatestBids()));
        AuctionResult intermediate = vcgAuction.getAuctionResult();
        assertThat(intermediate.getAllocation().getTotalAllocationValue())
                .isLessThan(first.getAllocation().getTotalAllocationValue());

        AuctionResult second = cca.getAuctionResult();
        assertThat(cca.isClockPhaseCompleted()).isTrue();
        assertThat(cca.hasNextSupplementaryRound()).isFalse();
        assertThat(second.getAllocation().getTotalAllocationValue())
                .isLessThanOrEqualTo(first.getAllocation().getTotalAllocationValue());

    }
}
