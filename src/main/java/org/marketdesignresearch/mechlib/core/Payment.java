package org.marketdesignresearch.mechlib.core;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.marketdesignresearch.mechlib.core.bidder.Bidder;
import org.marketdesignresearch.mechlib.metainfo.MetaInfo;
import org.marketdesignresearch.mechlib.metainfo.MetaInfoResult;
import org.springframework.data.annotation.PersistenceConstructor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * The payment has exactly the same bidder Set as its corresponding Allocation
 *
 * Map of bidder to payments. One payment per bidder. Payment may be 0 and
 * allocation may of payment may be empty
 * 
 * @author Benedikt
 * 
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({ @PersistenceConstructor }))
@EqualsAndHashCode(of = "paymentMap")
@ToString
public final class Payment implements MetaInfoResult {
	public static final Payment ZERO = new Payment(Collections.emptyMap(), new MetaInfo());

	private final Set<Bidder> bidders;
	private final Map<UUID, BidderPayment> paymentMap;
	@Getter
	private final MetaInfo metaInfo;

	public Payment(Map<Bidder, BidderPayment> bidderPaymentMap, MetaInfo metaInfo) {
		this.bidders = ImmutableSet.copyOf(bidderPaymentMap.keySet());
		Map<UUID, BidderPayment> map = new LinkedHashMap<>();
		bidderPaymentMap.forEach((k, v) -> map.put(k.getId(), v));
		this.paymentMap = ImmutableMap.copyOf(map);
		this.metaInfo = metaInfo;
	}

	public BigDecimal getTotalPayments() {
		return paymentMap.values().stream().map(BidderPayment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public Map<Bidder, BidderPayment> getPaymentMap() {
		Map<Bidder, BidderPayment> map = new LinkedHashMap<>();
		paymentMap.forEach((k, v) -> map.put(getBidder(k), v));
		return map;
	}

	public BidderPayment paymentOf(Bidder bidder) {
		return paymentMap.getOrDefault(bidder.getId(), BidderPayment.ZERO_PAYMENT);
	}

	public Payment merge(Payment other) {
		Map<Bidder, BidderPayment> paymentMap = new LinkedHashMap<>();
		for (Bidder bidder : Sets.union(getPaymentMap().keySet(), other.getPaymentMap().keySet())) {
			paymentMap.put(bidder,
					new BidderPayment(paymentOf(bidder).getAmount().add(other.paymentOf(bidder).getAmount())));
		}
		return new Payment(paymentMap, metaInfo.join(other.metaInfo));
	}

	private Bidder getBidder(UUID id) {
		return bidders.stream().filter(b -> b.getId().equals(id)).findAny().orElseThrow(NoSuchElementException::new);
	}
}
