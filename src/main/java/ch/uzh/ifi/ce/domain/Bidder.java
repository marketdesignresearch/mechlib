package ch.uzh.ifi.ce.domain;


import java.math.BigDecimal;


public interface Bidder {

    String getId();

    BigDecimal getValue(Bundle bundle);

}
