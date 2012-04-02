/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;
import com.wordnik.swagger.jaxrs.JavaHelp;

import org.apache.log4j.Logger;

import org.openide.util.io.ReaderInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.cismet.cids.custom.sudplan.geocpmrest.io.ExecutionStatus;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMException;
import de.cismet.cids.custom.sudplan.geocpmrest.io.GeoCPMUtils;
import de.cismet.cids.custom.sudplan.geocpmrest.io.ImportConfig;
import de.cismet.cids.custom.sudplan.geocpmrest.io.ImportStatus;
import de.cismet.cids.custom.sudplan.geocpmrest.io.Rainevent;
import de.cismet.cids.custom.sudplan.geocpmrest.io.SimulationConfig;
import de.cismet.cids.custom.sudplan.geocpmrest.io.SimulationResult;
import de.cismet.cids.custom.sudplan.wupp.geocpm.ie.GeoCPMAusImport;
import de.cismet.cids.custom.sudplan.wupp.geocpm.ie.GeoCPMExport;
import de.cismet.cids.custom.sudplan.wupp.geocpm.ie.GeoCPMImport;

import de.cismet.tools.FileUtils;

/**
 * DOCUMENT ME!
 *
 * @author   Martin Scholl (martin.scholl@cismet.de)
 * @author   Benjamin Friedrich (benjamin.friedrich@cismet.de)
 * @version  $Revision$, $Date$
 */
@Path("/GeoCPM.json")
@Api(
    value = "/GeoCPM",
    description = "Service wrapper for the GeoCPM and the DYNA model component"
)
@Produces({ "application/json" })
public final class GeoCPMRestServiceImpl extends JavaHelp implements GeoCPMService {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GeoCPMRestServiceImpl.class);

    public static final String PARAM_RUN_ID = "runId"; // NOI18N

    public static final String PATH_IMPORT_CFG = "/importConfiguration"; // NOI18N
    public static final String PATH_START_SIM = "/startSimulation";      // NOI18N
    public static final String PATH_GET_RESULTS = "/getResults";         // NOI18N
    public static final String PATH_GET_STATUS = "/getStatus";           // NOI18N
    public static final String PATH_CLEANUP = "/cleanup";                // NOI18N

    private static final String GEOCPM_EXE = "c:\\winkanal\\bin\\dyna.exe";                              // NOI18N
    private static final String LAUNCHER_EXE = "c:\\users\\wupp-model\\desktop\\launcher\\launcher.exe"; // NOI18N
    private static final String DB_PASSWORD = "cismetz12";                                               // NOI18N

    // FIXME: set right DV url
    private static final String DB_URL = "jdbc:postgresql://kif:5432/simple_geocpm_test_db3"; //
                                                                                              // "jdbc:postgresql://192.168.100.12:5432/sudplan_wupp";
                                                                                              // // NOI18N
                                                                                              // //"jdbc:postgresql://kif:5432/simple_geocpm_test_db2";
                                                                                              //// NOI18N private static

    private static final String DB_USERNAME = "postgres"; // NOI18N

    private static final String REST_URL = "http://sudplanwp6.cismet.de/geoserver"; // NOI18N
    private static final String REST_USER = "admin";                                // NOI18N
    private static final String REST_PWD = "cismetz12";                             // NOI18N
    private static final String WORKSPACE = "sudplan";                              // NOI18N

    private static final String WMS_GETCAPABILITIES =
        "http://sudplanwp6.cismet.de/geoserver/ows?service=wms&version=1.1.1&request=GetCapabilities";

    //~ Methods ----------------------------------------------------------------

    @PUT
    @Path(PATH_IMPORT_CFG)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Import GeoCPM configuration")
    @ApiErrors(
        value = {
                @ApiError(
                    code = 450,
                    reason = "Given ImportConfig is null or invalid"
                ),
                @ApiError(
                    code = 550,
                    reason = "An error occurs during the configuration import"
                )
            }
    )
    @Override
    public ImportStatus importConfiguration(
            @ApiParam(
                value = "Import configuration containing all information required to perfom a GeoCPM import",
                required = true
            ) final ImportConfig cfg) throws GeoCPMException, IllegalArgumentException {
        if (cfg == null) {
            throw new IllegalArgumentException("cfg must not be null");             // NOI18N
        } else if (cfg.getGeocpmData() == null) {
            throw new IllegalArgumentException("geocpm cfg data must not be null"); // NOI18N
        }

        try {
            // FIXME: add support for plain text data as specified in D6.2.2
            // FIXME: add dyna support
            // TODO: reintroduce compression?
// final GZIPInputStream geocpmIS = new GZIPInputStream(new ReaderInputStream(
// new StringReader(cfg.getGeocpmData()),
// "windows-1256"));
//
// final GZIPInputStream dynaIS = new GZIPInputStream(new ReaderInputStream(
// new StringReader(cfg.getDynaData()),
// "windows-1256"));

            final ReaderInputStream geocpmIS = new ReaderInputStream(
                    new StringReader(cfg.getGeocpmData()),
                    "windows-1256");

            final ReaderInputStream dynaIS = new ReaderInputStream(
                    new StringReader(cfg.getDynaData()),
                    "windows-1256");

            final ByteArrayInputStream geocpmID = new ByteArrayInputStream(cfg.getGeocpmIData());
            final ByteArrayInputStream geocpmFD = new ByteArrayInputStream(cfg.getGeocpmFData());
            final ByteArrayInputStream geocpmSD = new ByteArrayInputStream(cfg.getGeocpmSData());

            final GeoCPMImport geoCPMImport = new GeoCPMImport(
                    geocpmIS,
                    dynaIS,
                    geocpmID,
                    geocpmFD,
                    geocpmSD,
                    cfg.getGeocpmFolder(),
                    cfg.getDynaFolder(),
                    DB_USERNAME,
                    DB_PASSWORD,
                    DB_URL);
            final int cfgId = geoCPMImport.doImport();

            return new ImportStatus(cfgId);
        } catch (final Exception e) {
            final String message = "cannot import configuration(s): " + cfg; // NOI18N
            LOG.error(message, e);

            throw new GeoCPMException(message, e);
        }
    }

    @POST
    @Path(PATH_START_SIM)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Starts a simulation with the given configuration")
    @ApiErrors(
        value = {
                @ApiError(
                    code = 450,
                    reason = "Given SimulationConfig is null or invalid"
                ),
                @ApiError(
                    code = 550,
                    reason = "An error occurs during reading the simualation start setup"
                )
            }
    )
    @Override
    public ExecutionStatus startSimulation(
            @ApiParam(
                value =
                    "The configuration for this simulation containing information about the GeoCPM configuration to use, which rain event etc.",
                required = true
            ) final SimulationConfig cfg) throws GeoCPMException, IllegalArgumentException {
        if (cfg == null) {
            throw new IllegalArgumentException("cfg must not be null");       // NOI18N
        } else if (cfg.getRainevent() == null) {
            throw new IllegalArgumentException("rainevent must not be null"); // NOI18N
        }

        final File tmpDir = new File(System.getProperty("java.io.tmpdir"));           // NOI18N
        final File outDir = new File(tmpDir, "geocpm_" + System.currentTimeMillis()); // NOI18N

        try {
            if (!outDir.mkdir()) {
                throw new IOException("cannot create tmp dir"); // NOI18N
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Start GeoCPM export");
            }
            final GeoCPMExport export = new GeoCPMExport(cfg.getGeocpmCfg(), outDir, DB_USERNAME, DB_PASSWORD, DB_URL);
            export.doExport();
            if (LOG.isDebugEnabled()) {
                LOG.debug("GeoCPM export has been finished successfully");

                LOG.debug("Start DYNA export");
            }

            final Rainevent rainEvent = cfg.getRainevent();
            export.generateDYNA(rainEvent.getInterval(), rainEvent.getPrecipitations());
            if (LOG.isDebugEnabled()) {
                LOG.debug("DYNA export has been finished successfully");
            }
        } catch (final Exception e) {
            final String message = "error reading simulation configuration: " + cfg; // NOI18N
            LOG.error(message, e);

            throw new GeoCPMException(message, e);
        }

        final Properties exportMetaData = GeoCPMUtils.getExportMetaData(outDir);

        final String dynaFolder = exportMetaData.getProperty("dyna_folder");
        if (dynaFolder == null) {
            final String message = "No entry for DYNA output folder in export meta data";
            LOG.error(message);
            throw new GeoCPMException(message);
        }

        final File workingDir = new File(outDir.getAbsolutePath(), dynaFolder);

        try {
            final String command = LAUNCHER_EXE
                        + " -w "        // NOI18N
                        + "\"" + workingDir.getAbsolutePath() + "\""
                        + " -a "        // NOI18N
                        + GEOCPM_EXE
                        + " --pid"      // NOI18N
                        + " --killWER"; // NOI18N

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

            // TODO GeoCPM.ein file is not located in outdir
            final String taskId = GeoCPMUtils.createId(new File(outDir, "GeoCPM.ein"), GeoCPMUtils.readPid(workingDir));

            return new ExecutionStatus(ExecutionStatus.STARTED, taskId);
        } catch (final Exception e) {
            final String message = "error starting simulation: " + cfg; // NOI18N
            LOG.error(message, e);

            return new ExecutionStatus(ExecutionStatus.FAILED, null);
        }
    }

    @GET
    @Path(PATH_GET_STATUS)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Requests the status for a previously started simulation")
    @ApiErrors(
        value = {
                @ApiError(
                    code = 450,
                    reason = "Given task identifier is null or invalid"
                ),
                @ApiError(
                    code = 550,
                    reason = "An error occurs during reading the status from the workspace"
                )
            }
    )
    @Override
    public ExecutionStatus getStatus(
            @ApiParam(
                value = "The task identifier that was included in the 'startSimulation' response",
                required = true
            )
            @QueryParam(PARAM_RUN_ID)
            final String runId) throws GeoCPMException, IllegalArgumentException {
        if ((runId == null) || runId.isEmpty()) {
            throw new IllegalArgumentException("runId must not be null or empty"); // NOI18N
        }

        final int runPid;
        final int wdPid;
        final File workingDir;

        workingDir = GeoCPMUtils.getWorkingDir(runId);

        final Properties exportMetaData = GeoCPMUtils.getExportMetaData(workingDir);

        final String dynaFolder = exportMetaData.getProperty(GeoCPMExport.PROP_DYNA_FOLDER);
        if (dynaFolder == null) {
            final String message = "No entry for DYNA output folder in export meta data";
            LOG.error(message);
            throw new GeoCPMException(message);
        }

        final String geocpmFolder = exportMetaData.getProperty(GeoCPMExport.PROP_GEOCPM_FOLDER);
        if (geocpmFolder == null) {
            final String message = "No entry for GeoCPM output folder in export meta data";
            LOG.error(message);
            throw new GeoCPMException(message);
        }

        runPid = GeoCPMUtils.getPid(runId);
        wdPid = GeoCPMUtils.readPid(new File(workingDir, dynaFolder));

        if (runPid != wdPid) {
            final String message = "working dir pid and runid pid mismatch: " + runId; // NOI18N
            LOG.error(message);
            throw new GeoCPMException(message);
        }

        final ExecutionStatus status = GeoCPMUtils.getExecutionStatus(new File(workingDir, geocpmFolder), wdPid);
        status.setTaskId(runId);

        return status;
    }

    @GET
    @Path(PATH_GET_RESULTS)
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Requests the results of a previously started simulation")
    @ApiErrors(
        value = {
                @ApiError(
                    code = 450,
                    reason = "Given task identifier is null or invalid"
                ),
                @ApiError(
                    code = 550,
                    reason = "An error occurs during reading and processing the simulation from the workspace"
                )
            }
    )
    @Override
    public SimulationResult getResults(
            @ApiParam(
                value = "The task identifier that was included in the 'startSimulation' response",
                required = true
            )
            @QueryParam(PARAM_RUN_ID)
            final String runId) throws GeoCPMException, IllegalArgumentException, IllegalStateException {
        if ((runId == null) || runId.isEmpty()) {
            throw new IllegalArgumentException("runId must not be null or empty"); // NOI18N
        }

        final ExecutionStatus status = getStatus(runId);
        if (ExecutionStatus.FINISHED.equals(status.getStatus())) {
            try {
                final File workingDir = GeoCPMUtils.getWorkingDir(runId);

                final Properties exportMetaData = GeoCPMUtils.getExportMetaData(workingDir);

                final String geocpmEinFolderName = exportMetaData.getProperty(GeoCPMExport.PROP_GEOCPM_FOLDER);
                if (geocpmEinFolderName == null) {
                    final String message = "No export meta data entry for folder containing GeoCPM.EIN";
                    LOG.error(message);
                    throw new GeoCPMException(message);
                }

                final String configId = exportMetaData.getProperty(GeoCPMExport.PROP_CONFIG_ID);
                if (configId == null) {
                    final String message = "No configuration id entry for folder containing GeoCPM.EIN";
                    LOG.error(message);
                    throw new GeoCPMException(message);
                }

                final GeoCPMAusImport ausImport = new GeoCPMAusImport(
                        workingDir,
                        DB_USERNAME,
                        DB_PASSWORD,
                        DB_URL,
                        REST_USER,
                        REST_PWD,
                        REST_URL,
                        WORKSPACE);
                ausImport.go();

                // -----

                final SimulationResult result = new SimulationResult();
                result.setTaskId(runId);
                result.setGeocpmInfo(GeoCPMUtils.readInfo(workingDir));
                result.setWmsGetCapabilitiesRequest(WMS_GETCAPABILITIES);
                result.setLayerName(ausImport.getLayerName());

                return result;
            } catch (final Exception e) {
                final String message = "cannot get results: " + runId; // NOI18N
                LOG.error(message, e);
                throw new GeoCPMException(message, e);
            }
        } else {
            throw new IllegalStateException("cannot get results if not in finished state:" + runId); // NOI18N
        }
    }

    // we have to use POST instead of DELETE, see
    // http://jersey.576304.n2.nabble.com/Error-HTTP-method-DELETE-doesn-t-support-output-td4513829.html
    @POST
    @Path(PATH_CLEANUP)
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Cleans the workspace of a previously finished simulation")
    @ApiErrors(
        value = {
                @ApiError(
                    code = 450,
                    reason = "Task identifier refers to a task that is still running"
                ),
                @ApiError(
                    code = 451,
                    reason = "Given task identifier is null or invalid"
                ),
                @ApiError(
                    code = 550,
                    reason = "An error occurs during workspace cleanup"
                )
            }
    )
    @Override
    public void cleanup(
            @ApiParam(
                value = "The task identifier that was included in the 'startSimulation' response",
                required = true
            ) final String runId) throws GeoCPMException, IllegalArgumentException, IllegalStateException {
        final ExecutionStatus status = getStatus(runId);
        if (ExecutionStatus.FINISHED.equals(status.getStatus())) {
            try {
                final File workingDir = GeoCPMUtils.getWorkingDir(runId);
                FileUtils.deleteDir(workingDir);
            } catch (final Exception ex) {
                final String message = "cannot delete run directory for runid: " + runId; // NOI18N
                LOG.warn(message, ex);
                throw new GeoCPMException(message, ex);
            }
        } else {
            throw new IllegalStateException("simulation with id '" + runId + "' is not in finished state"); // NOI18N
        }
    }
}
