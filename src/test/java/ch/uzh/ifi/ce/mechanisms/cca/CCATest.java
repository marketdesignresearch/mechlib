package ch.uzh.ifi.ce.mechanisms.cca;

import ch.uzh.ifi.ce.demandquery.DiscreteDemandQuery;
import ch.uzh.ifi.ce.domain.AuctionInstance;
import ch.uzh.ifi.ce.domain.Bid;
import ch.uzh.ifi.ce.domain.Good;
import ch.uzh.ifi.ce.domain.cats.CATSAdapter;
import ch.uzh.ifi.ce.domain.cats.CATSAuction;
import ch.uzh.ifi.ce.domain.cats.CATSParser;
import ch.uzh.ifi.ce.domain.cats.Domain;
import ch.uzh.ifi.ce.mechanisms.AuctionMechanism;
import ch.uzh.ifi.ce.demandquery.DemandQuery;
import ch.uzh.ifi.ce.mechanisms.AuctionResult;
import ch.uzh.ifi.ce.mechanisms.cca.priceupdate.PriceUpdater;
import ch.uzh.ifi.ce.mechanisms.cca.priceupdate.SimpleRelativePriceUpdate;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class CCATest {
    @Test
    public void testCCAWithCATSAuction() throws IOException {
        Path catsFile = Paths.get("src/test/resources/0000.txt");
        CATSParser parser = new CATSParser();
        CATSAuction catsAuction = parser.readCatsAuctionBean(catsFile);
        CATSAdapter adapter = new CATSAdapter();
        Domain domain = adapter.adaptToDomain(catsAuction);
        Set<Good> goods = domain.getGoods();
        DemandQuery genericDemandQuery = new DiscreteDemandQuery(domain.getValues());
        CCAuction cca = new CCAuction(goods, domain.getBidders(), genericDemandQuery);
        PriceUpdater priceUpdater = new SimpleRelativePriceUpdate().withInitialUpdate(BigDecimal.TEN);
        cca.setPriceUpdater(priceUpdater);
        AuctionResult auctionResult = cca.getAuctionResult();
        // Compare to direct CPLEX result
        assertThat(cca.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(1787.8971);
        log.info(cca.getAllocation().toString());

    }
}
