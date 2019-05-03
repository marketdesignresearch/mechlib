package org.marketdesignresearch.mechlib.mechanisms.cca;

import org.marketdesignresearch.mechlib.demandquery.DiscreteDemandQuery;
import org.marketdesignresearch.mechlib.domain.Allocation;
import org.marketdesignresearch.mechlib.domain.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.domain.cats.CATSAuction;
import org.marketdesignresearch.mechlib.domain.cats.CATSParser;
import org.marketdesignresearch.mechlib.domain.Domain;
import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.mechanisms.cca.priceupdate.PriceUpdater;
import org.marketdesignresearch.mechlib.mechanisms.cca.priceupdate.SimpleRelativePriceUpdate;
import org.marketdesignresearch.mechlib.mechanisms.cca.round.supplementaryround.ProfitMaximizingSupplementaryRound;
import org.marketdesignresearch.mechlib.mechanisms.vcg.VCGAuction;
import org.marketdesignresearch.mechlib.mechanisms.vcg.XORVCGAuction;
import org.marketdesignresearch.mechlib.winnerdetermination.XORWinnerDetermination;
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
        CCAuction cca = new CCAuction(domain.getGoods(), domain.getBidders(), new DiscreteDemandQuery(domain.getBidders()));
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

        CCAuction cca = new CCAuction(domain.getGoods(), domain.getBidders(), new DiscreteDemandQuery(domain.getBidders()));
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(2));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(3));
        cca.addSupplementaryRound(new ProfitMaximizingSupplementaryRound(cca).withNumberOfSupplementaryBids(4));
        Allocation previousAllocation = Allocation.EMPTY_ALLOCATION;
        while (!cca.isClockPhaseCompleted()) {
            cca.nextClockRound();
            Allocation allocation = new XORWinnerDetermination(cca.getLatestBids()).getAllocation();
            assertThat(allocation.getTotalAllocationValue()).isLessThanOrEqualTo(resultIncludingAllBids.getAllocation().getTotalAllocationValue());
            assertThat(allocation.getTotalAllocationValue()).isGreaterThanOrEqualTo(previousAllocation.getTotalAllocationValue());
            previousAllocation = allocation;
        }
        while (cca.hasNextSupplementaryRound()) {
            cca.nextSupplementaryRound();
            Allocation allocation = new XORWinnerDetermination(cca.getLatestBids()).getAllocation();
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
        CCAuction cca = new CCAuction(domain.getGoods(), domain.getBidders(), new DiscreteDemandQuery(domain.getBidders()));
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

        VCGAuction vcgAuction = new XORVCGAuction(cca.getLatestBids());
        AuctionResult intermediate = vcgAuction.getAuctionResult();
        assertThat(intermediate.getAllocation().getTotalAllocationValue())
                .isLessThan(first.getAllocation().getTotalAllocationValue());

        AuctionResult second = cca.getAuctionResult();
        assertThat(cca.isClockPhaseCompleted()).isTrue();
        assertThat(cca.hasNextSupplementaryRound()).isFalse();
        assertThat(second.getAllocation().getTotalAllocationValue())
                .isEqualTo(first.getAllocation().getTotalAllocationValue());

    }
}
