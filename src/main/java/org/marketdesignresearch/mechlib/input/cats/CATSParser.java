package org.marketdesignresearch.mechlib.input.cats;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CATSParser {

    public CATSAuction readCatsAuctionBean(Path file) throws IOException {
        InputStream fileStream = Files.newInputStream(file);
        CATSAuction auctionBean = new CATSAuction();
        try (Scanner scanner = new Scanner(fileStream)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.startsWith("%") && !line.isEmpty()) {
                    if (line.startsWith("goods")) {
                        auctionBean.setNumberOfGoods(processIntLine(line));
                    } else if (line.startsWith("bids")) {
                        auctionBean.setNumberOfBids(processIntLine(line));
                    } else if (line.startsWith("dummy")) {
                        auctionBean.setNumberOfDummyGoods(processIntLine(line));
                    } else {
                        auctionBean.addCatsBid(processBidLine(line));
                    }

                } else if (line.contains("Distribution: ")) {
                    int start = line.indexOf("Distribution: ") + "Distribution: ".length();
                    int end = line.indexOf(";", start);
                    if (end > start) {
                        auctionBean.setDistribution(line.subSequence(start, end));
                    }
                }
            }
        }
        auctionBean.setFileName(file.getFileName());
        return auctionBean;
    }

    private CATSBid processBidLine(String line) {
        CATSBid bidBean = new CATSBid();
        Scanner lineScanner = new Scanner(line);
        bidBean.setId(lineScanner.nextInt());

        String valueString = lineScanner.hasNext() ? lineScanner.next() : null;
        bidBean.setAmount(NumberUtils.isCreatable(valueString) ? new BigDecimal(valueString) : BigDecimal.ZERO);
        List<Integer> goods = new LinkedList<>();
        while (lineScanner.hasNextInt()) {
            goods.add(lineScanner.nextInt());
        }
        bidBean.setGoodIds(goods);
        lineScanner.close();
        return bidBean;
    }

    private int processIntLine(String line) {

        Scanner lineScanner = new Scanner(line);
        // discard one token
        lineScanner.next();

        int result = lineScanner.nextInt();
        lineScanner.close();
        return result;
    }

}
