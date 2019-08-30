package org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules;

import org.marketdesignresearch.mechlib.core.Outcome;
import org.marketdesignresearch.mechlib.core.Payment;
import edu.harvard.econcs.jopt.solver.IMIP;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentationable;

public interface CorePaymentNorm extends MipInstrumentationable {
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
    Outcome getReferencePoint();
}
