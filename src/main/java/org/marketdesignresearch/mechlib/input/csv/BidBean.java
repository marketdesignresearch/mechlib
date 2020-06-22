package org.marketdesignresearch.mechlib.input.csv;

import java.math.BigDecimal;

import org.apache.commons.collections4.MultiValuedMap;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.Setter;

/**
 * This helper class represents a row in a CSV file to read in a collection of bids.
 * The file has the following structure:
 * <code>
 *     Bidder,Bid,Item1,Item2,Item3
 *     A,80,0,0,1
 *     B,70,1,1,0
 * </code>
 * The first row represents a bid of 80 by bidder A for bundle {Item3}.
 * The second row represents a bid of 70 by bidder B for bundle {Item1,Item2}
 *
 * The columns "Bidder" and "Bid" are required, and all the other columns are considered
 * as items (and at least one is required).
 */
@Getter @Setter
public class BidBean {
    @CsvBindByName(column = "Bidder", required = true)
    private String bidder;
    @CsvBindByName(column = "Bid", required = true)
    private BigDecimal bid;
    @CsvBindAndJoinByName(column = ".+", elementType = Integer.class, required = true)
    private MultiValuedMap<String, Integer> goods;
}
