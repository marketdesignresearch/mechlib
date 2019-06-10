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
    CCA_VCG,
    CCA_CCG,
    PVM;

    // TODO: Find the right enum usage to have all common auction formats
    public Auction getAuction(Domain domain) {
        switch (this) {
            case SINGLE_ITEM_FIRST_PRICE:                                                       // Single Item --- First Price
            case SIMULTANEOUS_FIRST_PRICE:                                                      // Multi Item  --- Simultaneous --- First Price
                return new Auction(domain, MechanismType.FIRST_PRICE);
            case SINGLE_ITEM_SECOND_PRICE:                                                      // Single Item --- Second Price
            case SIMULTANEOUS_SECOND_PRICE:                                                     // Multi Item  --- Simultaneous --- Second Price
                return new Auction(domain, MechanismType.SECOND_PRICE);
            case SEQUENTIAL_FIRST_PRICE:                                                        // Multi Item  --- Sequential --- First Price
                return new SequentialAuction(domain, MechanismType.FIRST_PRICE);
            case SEQUENTIAL_SECOND_PRICE:                                                       // Multi Item  --- Sequential --- Second Price
                return new SequentialAuction(domain, MechanismType.SECOND_PRICE);
            case CCA_VCG:                                                                           // Multi Item  --- CCA --- VCG/CCG
                return new CCAuction(domain, MechanismType.VCG_XOR);
            case CCA_CCG:                                                                           // Multi Item  --- CCA --- VCG/CCG
                return new CCAuction(domain, MechanismType.CCG);
            case PVM:                                                                           // Multi Item  --- PVM --- VCG/CCG
                throw new UnsupportedOperationException("PVM is yet to be implemented!");
            default:
                throw new UnsupportedOperationException();


        }
    }
}
