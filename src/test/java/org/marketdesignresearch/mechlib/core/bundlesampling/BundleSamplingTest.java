package org.marketdesignresearch.mechlib.core.bundlesampling;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.input.cats.CATSAdapter;
import org.marketdesignresearch.mechlib.input.cats.CATSAuction;
import org.marketdesignresearch.mechlib.input.cats.CATSParser;

public class BundleSamplingTest {
	@Test
	public void testUniformRandomBundleSampling() throws IOException {
		Path catsFileStream = Paths.get("src/test/resources/day2007example.txt");
		CATSParser parser = new CATSParser();
		CATSAuction catsAuction = parser.readCatsAuctionBean(catsFileStream);
		CATSAdapter adapter = new CATSAdapter();
		Domain domain = adapter.adaptToDomain(catsAuction);
		BundleSampling sampling = new UniformRandomBundleSampling(new Random(2));
		Bundle sampled = domain.getSampledBundle(sampling);
		assertThat(sampled.getSingleQuantityGoods(),
				is(List.of(domain.getGood("0"), domain.getGood("1"), domain.getGood("3"))));
		System.out.println(sampled);
	}

	@Test
	public void testLimitedSizeRandomBundleSampling() throws IOException {
		Path catsFileStream = Paths.get("src/test/resources/day2007example.txt");
		CATSParser parser = new CATSParser();
		CATSAuction catsAuction = parser.readCatsAuctionBean(catsFileStream);
		CATSAdapter adapter = new CATSAdapter();
		Domain domain = adapter.adaptToDomain(catsAuction);
		BundleSampling sampling = new LimitedSizeRandomBundleSampling(2, new Random(2));
		Bundle sampled = domain.getSampledBundle(sampling);
		assertThat(sampled.getSingleQuantityGoods(), is(List.of(domain.getGood("0"), domain.getGood("1"))));
		System.out.println(sampled);
	}

}
