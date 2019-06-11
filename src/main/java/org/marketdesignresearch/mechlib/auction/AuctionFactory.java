package org.marketdesignresearch.mechlib.auction;

import org.marketdesignresearch.mechlib.domain.Domain;
import org.marketdesignresearch.mechlib.mechanisms.MechanismType;
import org.marketdesignresearch.mechlib.auction.cca.CCAuction;
import org.marketdesignresearch.mechlib.auction.sequential.SequentialAuction;

public enum AuctionFactory {
    SINGLE_ITEM_FIRST_PRICE,
    SINGLE_ITEM_SECOND_PRICE,
    SEQUENTIAL_FIRST_PRICE,
    SEQUENTIAL_SECOND_PRICE,
    SIMULTANEOUS_FIRST_PRICE,
    SIMULTANEOUS_SECOND_PRICE,
    VCG_XOR,
    VCG_OR,
    CCA_VCG,
    CCA_CCG,
    PVM;

    public Auction getAuction(Domain domain) {
        switch (this) {
            case SINGLE_ITEM_FIRST_PRICE:
            case SIMULTANEOUS_FIRST_PRICE:
                return new Auction(domain, MechanismType.FIRST_PRICE);
            case SINGLE_ITEM_SECOND_PRICE:
            case SIMULTANEOUS_SECOND_PRICE:
                return new Auction(domain, MechanismType.SECOND_PRICE);
            case SEQUENTIAL_FIRST_PRICE:
                return new SequentialAuction(domain, MechanismType.FIRST_PRICE);
            case SEQUENTIAL_SECOND_PRICE:
                return new SequentialAuction(domain, MechanismType.SECOND_PRICE);
            case VCG_XOR:
                return new Auction(domain, MechanismType.VCG_XOR);
            case VCG_OR:
                return new Auction(domain, MechanismType.VCG_OR);
            case CCA_VCG:
                return new CCAuction(domain, MechanismType.VCG_XOR);
            case CCA_CCG:
                return new CCAuction(domain, MechanismType.CCG);
            case PVM:
                throw new UnsupportedOperationException("PVM is yet to be implemented!");
            default:
                throw new UnsupportedOperationException();
        }
    }
}
