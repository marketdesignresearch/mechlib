package org.marketdesignresearch.mechlib.input.cats;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CATSAuction implements Serializable {

	private static final long serialVersionUID = -3253471183746973026L;
	private int numberOfGoods;
	private int numberOfBids;
	private int numberOfDummyGoods;
	@Setter(AccessLevel.NONE)
	private final List<CATSBid> catsBids = new ArrayList<>();
	@Setter(AccessLevel.NONE)
	private String distribution = null;
	private Path fileName;

	public void addCatsBid(CATSBid catsBid) {
		this.catsBids.add(catsBid);
	}

	public void setDistribution(CharSequence subSequence) {
		distribution = subSequence.toString();
	}

}
