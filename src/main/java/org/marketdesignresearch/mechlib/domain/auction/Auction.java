package org.marketdesignresearch.mechlib.domain.auction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.marketdesignresearch.mechlib.domain.Bidder;
import org.marketdesignresearch.mechlib.domain.Good;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class Auction {
    @Getter
    private final Set<Bidder> bidders;
    @Getter
    private final List<Good> goods;
    @Getter
    private final List<AuctionRound> auctionRounds = new ArrayList<>();
}
