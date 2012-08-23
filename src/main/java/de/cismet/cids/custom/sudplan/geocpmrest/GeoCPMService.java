/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest;

import de.cismet.cids.custom.sudplan.geocpmrest.io.ExecutionStatus;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMException;
import de.cismet.cids.custom.sudplan.geocpmrest.io.ImportConfig;
import de.cismet.cids.custom.sudplan.geocpmrest.io.ImportStatus;
import de.cismet.cids.custom.sudplan.geocpmrest.io.SimulationConfig;
import de.cismet.cids.custom.sudplan.geocpmrest.io.SimulationResult;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface GeoCPMService {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   cfg  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  GeoCPMException           DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    ImportStatus importConfiguration(final ImportConfig cfg) throws GeoCPMException, IllegalArgumentException;

    /**
     * DOCUMENT ME!
     *
     * @param   cfg  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  GeoCPMException           DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    ExecutionStatus startSimulation(final SimulationConfig cfg) throws GeoCPMException, IllegalArgumentException;

    /**
     * DOCUMENT ME!
     *
     * @param   runId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  GeoCPMException           DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    ExecutionStatus getStatus(final String runId) throws GeoCPMException, IllegalArgumentException;

    /**
     * DOCUMENT ME!
     *
     * @param   runId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  GeoCPMException           DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    SimulationResult getResults(final String runId) throws GeoCPMException,
        IllegalArgumentException,
        IllegalStateException;

    /**
     * DOCUMENT ME!
     *
     * @param   runId  DOCUMENT ME!
     *
     * @throws  GeoCPMException           DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    void cleanup(final String runId) throws GeoCPMException, IllegalArgumentException, IllegalStateException;
}
