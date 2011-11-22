/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest;

import javax.ws.rs.QueryParam;

import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMInput;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMOutput;
import de.cismet.cids.custom.sudplan.geocpmrest.io.Status;

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
     * @param  runId  DOCUMENT ME!
     */
    void deleteRun(@QueryParam(value = GeoCPMRestServiceImpl.PARAM_RUN_ID) final String runId);

    /**
     * DOCUMENT ME!
     *
     * @param   runId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    GeoCPMOutput getResults(@QueryParam(value = GeoCPMRestServiceImpl.PARAM_RUN_ID) final String runId);

    /**
     * DOCUMENT ME!
     *
     * @param   runId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Status getStatus(@QueryParam(value = GeoCPMRestServiceImpl.PARAM_RUN_ID) final String runId);

    /**
     * DOCUMENT ME!
     *
     * @param   input  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String runGeoCPM(final GeoCPMInput input);
}
