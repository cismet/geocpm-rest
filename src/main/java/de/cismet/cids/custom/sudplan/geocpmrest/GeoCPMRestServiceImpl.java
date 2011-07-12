/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMInput;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMOutput;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMUtils;
import de.cismet.cids.custom.sudplan.geocpmrest.io.Status;

import de.cismet.tools.FileUtils;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@Path("/GeoCPM")
public final class GeoCPMRestServiceImpl implements GeoCPMService {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GeoCPMRestServiceImpl.class);

    public static final String PARAM_RUN_ID = "runId"; // NOI18N
    public static final String PARAM_INPUT = "input";  // NOI18N

    public static final String PATH_RUN_GEOCPM = "/runGeoCPM";   // NOI18N
    public static final String PATH_GET_RESULTS = "/getResults"; // NOI18N
    public static final String PATH_GET_STATUS = "/getStatus";   // NOI18N
    public static final String PATH_DEL_RUN = "/deleteRun";      // NOI18N

    private static final String GEOCPM_EXE = "c:\\winkanal\\bin\\GeoCPM.exe";                            // NOI18N
    private static final String LAUNCHER_EXE = "c:\\users\\wupp-model\\desktop\\launcher\\launcher.exe"; // NOI18N

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   input  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  WebApplicationException  DOCUMENT ME!
     */
    @PUT
    @Path(PATH_RUN_GEOCPM)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Override
    public String runGeoCPM(final GeoCPMInput input) {
        try {
            final File outFile = GeoCPMUtils.writeInput(input);
            final String command = LAUNCHER_EXE
                        + " -w "       // NOI18N
                        + outFile.getParentFile().getAbsolutePath()
                        + " -a "       // NOI18N
                        + GEOCPM_EXE
                        + " --pid"     // NOI18N
                        + " --killWER" // NOI18N
                        + " --args "   // NOI18N
                        + outFile.getAbsolutePath();

            if (LOG.isDebugEnabled()) {
                LOG.debug("launching command: " + command); // NOI18N
            }

            final Process p = Runtime.getRuntime().exec(command);

            GeoCPMUtils.drainStreams(p);

            final int exitCode = p.waitFor();

            if (LOG.isDebugEnabled()) {
                LOG.debug("process exit code: " + exitCode); // NOI18N
            }

            if (exitCode != 0) {
                throw new IOException("process was not finished gracefully: " + exitCode); // NOI18N
            }

            return GeoCPMUtils.createId(outFile, GeoCPMUtils.readPid(outFile.getParentFile()));
        } catch (final Exception e) {
            if (e instanceof WebApplicationException) {
                throw (WebApplicationException)e;
            }

            final String message = "cannot run GeoCPM model: " + input;                                          // NOI18N
            LOG.error(message, e);
            throw new WebApplicationException(Response.status(500).entity(message + " | Exc: " + e.getMessage()) // NOI18N
                .build());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   runId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  WebApplicationException  DOCUMENT ME!
     */
    @GET
    @Path(PATH_GET_STATUS)
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Status getStatus(@QueryParam(PARAM_RUN_ID) final String runId) {
        try {
            final int runPid;
            final int wdPid;
            final File workingDir;

            try {
                workingDir = GeoCPMUtils.getWorkingDir(runId);
                runPid = GeoCPMUtils.getPid(runId);
                wdPid = GeoCPMUtils.readPid(workingDir);
            } catch (final Exception e) {
                final String message = "illegal id: " + runId; // NOI18N
                LOG.error(message, e);
                throw new WebApplicationException(Response.status(409).entity(message).build());
            }

            if (runPid != wdPid) {
                final String message = "working dir pid and runid pid mismatch: " + runId; // NOI18N
                LOG.error(message);
                throw new WebApplicationException(Response.status(409).entity(message).build());
            }

            return GeoCPMUtils.getExecutionStatus(workingDir, wdPid);
        } catch (final Exception e) {
            if (e instanceof WebApplicationException) {
                throw (WebApplicationException)e;
            }

            final String message = "cannot fetch status for runid: " + runId;                                    // NOI18N
            LOG.error(message, e);
            throw new WebApplicationException(Response.status(500).entity(message + " | Exc: " + e.getMessage()) // NOI18N
                .build());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   runId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  WebApplicationException  DOCUMENT ME!
     */
    @GET
    @Path(PATH_GET_RESULTS)
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public GeoCPMOutput getResults(@QueryParam(PARAM_RUN_ID) final String runId) {
        try {
            final int runPid;
            final int wdPid;
            final File workingDir;

            try {
                workingDir = GeoCPMUtils.getWorkingDir(runId);
                runPid = GeoCPMUtils.getPid(runId);
                wdPid = GeoCPMUtils.readPid(workingDir);
            } catch (final Exception e) {
                final String message = "illegal id: " + runId; // NOI18N
                LOG.error(message, e);
                throw new WebApplicationException(Response.status(409).entity(message).build());
            }

            if (runPid != wdPid) {
                final String message = "working dir pid and runid pid mismatch: " + runId; // NOI18N
                LOG.error(message);
                throw new WebApplicationException(Response.status(409).entity(message).build());
            }

            final Status status = GeoCPMUtils.getExecutionStatus(workingDir, runPid);

            if (status.status != Status.STATUS_FINISHED) {
                LOG.warn("requested results for unfinisehd run: " + runId);                                           // NOI18N
                throw new WebApplicationException(Response.status(409).entity("not finished yet: " + runId).build()); // NOI18N
            }

            return GeoCPMUtils.readOutput(workingDir);
        } catch (final Exception e) {
            if (e instanceof WebApplicationException) {
                throw (WebApplicationException)e;
            }

            final String message = "cannot fetch results for runid: " + runId;                                   // NOI18N
            LOG.error(message, e);
            throw new WebApplicationException(Response.status(500).entity(message + " | Exc: " + e.getMessage()) // NOI18N
                .build());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   runId  DOCUMENT ME!
     *
     * @throws  WebApplicationException  DOCUMENT ME!
     */
    // we have to use POST instead of DELETE, see
    // http://jersey.576304.n2.nabble.com/Error-HTTP-method-DELETE-doesn-t-support-output-td4513829.html
    @POST
    @Path(PATH_DEL_RUN)
    @Consumes(MediaType.TEXT_PLAIN)
    @Override
    public void deleteRun(final String runId) {
        try {
            final File workingDir = GeoCPMUtils.getWorkingDir(runId);
            FileUtils.deleteDir(workingDir);
        } catch (final Exception ex) {
            final String message = "cannot delete run directory for runid: " + runId; // NOI18N
            LOG.warn(message, ex);
            throw new WebApplicationException(Response.status(409).entity(message).build());
        }
    }
}
