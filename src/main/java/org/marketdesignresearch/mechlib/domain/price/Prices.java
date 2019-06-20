package org.marketdesignresearch.mechlib.domain.price;

import org.marketdesignresearch.mechlib.domain.Bundle;

import java.util.ArrayList;

public interface Prices {

    Prices NONE = new LinearPrices(new ArrayList<>());

    Price getPrice(Bundle bundle);

}
