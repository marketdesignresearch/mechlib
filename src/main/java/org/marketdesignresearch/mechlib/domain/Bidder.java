package org.marketdesignresearch.mechlib.domain;


import java.math.BigDecimal;


public interface Bidder {

    String getId();

    BigDecimal getValue(Bundle bundle);

}
