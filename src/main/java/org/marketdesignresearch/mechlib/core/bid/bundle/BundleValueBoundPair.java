package org.marketdesignresearch.mechlib.core.bid.bundle;

import java.math.BigDecimal;
import java.util.Set;

import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.springframework.data.annotation.PersistenceConstructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BundleValueBoundPair extends BundleValuePair{
	
	@Getter
	private final BigDecimal upperBound;
	
	/**
     * @param amount Bid amount
     * @param bundle Goods to bid on
     * @param id Same id as BundleValue
     */
    public BundleValueBoundPair(BigDecimal lowerBound, BigDecimal upperBound, Set<Good> bundle, String id) {
        this(lowerBound, upperBound, Bundle.of(bundle), id);
    }
    
    @PersistenceConstructor
    public BundleValueBoundPair(BigDecimal lowerBound, BigDecimal upperBound, Bundle bundle, String id) {
    	super(lowerBound,bundle,id);
    	this.upperBound = upperBound;
    }
    
    public BigDecimal getLowerBound() {
    	return this.getAmount();
    }
}
