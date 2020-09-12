package org.marketdesignresearch.mechlib.input.cats;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CATSBid implements Serializable {
	private static final long serialVersionUID = 7736295648019090804L;
	private int id;
	private BigDecimal amount;
	private List<Integer> goodIds;
}
