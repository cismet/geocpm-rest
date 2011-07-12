/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    @DELETE
    @Path(value = "/deleteRun")
    @Consumes(value = "test/plain")
    void deleteRun(@QueryParam(value = GeoCPMRestServiceImpl.PARAM_RUN_ID) final String runId);

    /**
     * DOCUMENT ME!
     *
     * @param   runId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @GET
    @Path(value = "/getResults")
    @Consumes(value = "text/plain")
    @Produces(value = "application/json")
    GeoCPMOutput getResults(@QueryParam(value = GeoCPMRestServiceImpl.PARAM_RUN_ID) final String runId);

    /**
     * DOCUMENT ME!
     *
     * @param   runId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @GET
    @Path(value = "/getStatus")
    @Consumes(value = "text/plain")
    @Produces(value = "application/json")
    Status getStatus(@QueryParam(value = GeoCPMRestServiceImpl.PARAM_RUN_ID) final String runId);

    /**
     * DOCUMENT ME!
     *
     * @param   input  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @PUT
    @Path(value = "/runGeoCPM")
    @Consumes(value = "application/json")
    @Produces(value = "text/plain")
    String runGeoCPM(final GeoCPMInput input);
}
