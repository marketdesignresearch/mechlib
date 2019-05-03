package org.marketdesignresearch.mechlib.mechanisms.ccg.paymentrules;

import org.marketdesignresearch.mechlib.mechanisms.AuctionResult;
import org.marketdesignresearch.mechlib.domain.Payment;
import edu.harvard.econcs.jopt.solver.IMIP;

public interface CorePaymentNorm {
    /**
     * Takes the existing constraints of the program the optional
     * prmaryConstraint</br> The objective terms of the program are ignored and
     * a new objective with respect to the norm is added.</br> Returns a Payment
     * vector that minimizes the distances regarding this norm and a given
     * reference Point.
     * 
     * @param program
     * @return
     */
    Payment minimizeDistance(IMIP program);

    /**
     * Takes the existing constraints of the mip</br>
     * 
     * @param mip
     * @return a objective that ensure the primary objective is fixed.
     */
    PrimaryObjective getPrimaryObjectiveConstraint(IMIP mip);

    /**
     * 
     * @return the reference point for this norm
     */
    AuctionResult getReferencePoint();
}
