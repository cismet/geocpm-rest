/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest;

import org.apache.log4j.Logger;

import org.openide.util.io.ReaderInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;



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
import de.cismet.cids.custom.sudplan.geocpmrest.io.SimulationConfig;
import de.cismet.cids.custom.sudplan.geocpmrest.io.SimulationResult;
import de.cismet.cids.custom.sudplan.wupp.geocpm.ie.GeoCPMExport;
import de.cismet.cids.custom.sudplan.wupp.geocpm.ie.GeoCPMImport;

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

    public static final String PATH_IMPORT_CFG = "/importConfiguration"; // NOI18N
    public static final String PATH_START_SIM = "/startSimulation";      // NOI18N
    public static final String PATH_GET_RESULTS = "/getResults";         // NOI18N
    public static final String PATH_GET_STATUS = "/getStatus";           // NOI18N
    public static final String PATH_CLEANUP = "/cleanup";                // NOI18N

//    private static final String GEOCPM_EXE = "c:\\winkanal\\bin\\GeoCPM.exe";
    private static final String GEOCPM_EXE = "c:\\winkanal\\bin\\dyna.exe";                              // NOI18N
    private static final String LAUNCHER_EXE = "c:\\users\\wupp-model\\desktop\\launcher\\launcher.exe"; // NOI18N

    private static final String DB_PASSWORD = "cismetz12"; // NOI18N 

    private static final String DB_URL = "jdbc:postgresql://192.168.100.12:5432/sudplan_wupp"; // NOI18N //"jdbc:postgresql://kif:5432/simple_geocpm_test_db2"; // NOI18N private static
                                                                                             
    private static final String DB_USERNAME = "postgres"; // NOI18N

    //~ Methods ----------------------------------------------------------------

    @PUT
    @Path(PATH_IMPORT_CFG)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public ImportStatus importConfiguration(final ImportConfig cfg) throws GeoCPMException, IllegalArgumentException {
        if (cfg == null) {
            throw new IllegalArgumentException("cfg must not be null");             // NOI18N
        } else if (cfg.getGeocpmData() == null) {
            throw new IllegalArgumentException("geocpm cfg data must not be null"); // NOI18N
        }

        try {
            // FIXME: add support for plain text data as specified in D6.2.2
            // FIXME: add dyna support
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
    @Override
    public ExecutionStatus startSimulation(final SimulationConfig cfg) throws GeoCPMException,
        IllegalArgumentException {
        if (cfg == null) {
            throw new IllegalArgumentException("cfg must not be null");       // NOI18N
        } else if (cfg.getRainevent() == null) {
            throw new IllegalArgumentException("rainevent must not be null"); // NOI18N
        }

        final File tmpDir = new File(System.getProperty("java.io.tmpdir"));           // NOI18N
        final File outDir = new File(tmpDir, "geocpm_" + System.currentTimeMillis()); // NOI18N

//        final File outFile;
        try {
            // FIXME: use dyna
// final File tmpDir = new File(System.getProperty("java.io.tmpdir"));           // NOI18N
// final File outDir = new File(tmpDir, "geocpm_" + System.currentTimeMillis()); // NOI18N
            if (!outDir.mkdir()) {
                throw new IOException("cannot create tmp dir"); // NOI18N
            }

//            outFile = new File(outDir, "GeoCPM.ein");// NOI18N

            final GeoCPMExport export = new GeoCPMExport(cfg.getGeocpmCfg(), outDir, DB_USERNAME, DB_PASSWORD, DB_URL);
            export.doExport();
        } catch (final Exception e) {
            final String message = "error reading simulation configuration: " + cfg; // NOI18N
            LOG.error(message, e);

            throw new GeoCPMException(message, e);
        }

        try {
            final String command = LAUNCHER_EXE
                        + " -w "                    // NOI18N
                        + outDir.getAbsolutePath()  // outFile.getParentFile().getAbsolutePath()
                        + " -a "                    // NOI18N
                        + GEOCPM_EXE
                        + " --pid"                  // NOI18N
                        + " --killWER"              // NOI18N
                        + " --args "                // NOI18N
                        + outDir.getAbsolutePath(); // outFile.getAbsolutePath();

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

//            final String taskId = GeoCPMUtils.createId(outFile, GeoCPMUtils.readPid(outFile.getParentFile()));
            final String taskId = GeoCPMUtils.createId(new File(outDir, "GeoCPM.ein"), GeoCPMUtils.readPid(outDir));

            return new ExecutionStatus(ExecutionStatus.STARTED, taskId);
        } catch (final Exception e) {
            final String message = "error starting simulation: " + cfg; // NOI18N
            LOG.error(message, e);

            return new ExecutionStatus(ExecutionStatus.FAILED, null);
        }
    }

    @GET
    @Path(PATH_GET_STATUS)
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public ExecutionStatus getStatus(@QueryParam(PARAM_RUN_ID) final String runId) throws GeoCPMException,
        IllegalArgumentException {
        if ((runId == null) || runId.isEmpty()) {
            throw new IllegalArgumentException("runId must not be null or empty"); // NOI18N
        }

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
                throw new IllegalArgumentException(message, e);
            }

            if (runPid != wdPid) {
                final String message = "working dir pid and runid pid mismatch: " + runId; // NOI18N
                LOG.error(message);
                throw new GeoCPMException(message);
            }

            final ExecutionStatus status = GeoCPMUtils.getExecutionStatus(workingDir, wdPid);
            status.setTaskId(runId);

            return status;
        } catch (final Exception e) {
            final String message = "cannot fetch status for runid: " + runId; // NOI18N
            LOG.error(message, e);
            throw new GeoCPMException(message, e);
        }
    }

    @GET
    @Path(PATH_GET_RESULTS)
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public SimulationResult getResults(@QueryParam(PARAM_RUN_ID) final String runId) throws GeoCPMException,
        IllegalArgumentException,
        IllegalStateException {
        if ((runId == null) || runId.isEmpty()) {
            throw new IllegalArgumentException("runId must not be null or empty"); // NOI18N
        }

        final ExecutionStatus status = getStatus(runId);
        if (ExecutionStatus.FINISHED.equals(status.getStatus())) {
            try {
                final File workingDir = GeoCPMUtils.getWorkingDir(runId);

                final SimulationResult result = new SimulationResult();
                result.setTaskId(runId);
                result.setGeocpmInfo(GeoCPMUtils.readInfo(workingDir));

                // FIXME: WMS layers to be added

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
    @Override
    public void cleanup(final String runId) throws GeoCPMException, IllegalArgumentException, IllegalStateException {
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
//
//    /**
//     * DOCUMENT ME!
//     *
//     * @param  args  DOCUMENT ME!
//     */
//    public static void main(final String[] args) {
//        try {
//            final Properties p = new Properties();
//            p.put("log4j.appender.Remote", "org.apache.log4j.net.SocketAppender");
//            p.put("log4j.appender.Remote.remoteHost", "localhost");
//            p.put("log4j.appender.Remote.port", "4445");
//            p.put("log4j.appender.Remote.locationInfo", "true");
//            p.put("log4j.rootLogger", "ALL,Remote");
//            org.apache.log4j.PropertyConfigurator.configure(p);
//
//            final ImportConfig config = new ImportConfig();
//
//            final File geocpmFDFile = new File(
//                    "/home/bfriedrich/Desktop/geocpm/2012-02-27/DYNA-GeoCPM_120131/GeoCPM_DVWK_T=100a Nullvariante/GEOCPMF.D");
//            final File geocpmSDFile = new File(
//                    "/home/bfriedrich/Desktop/geocpm/2012-02-27/DYNA-GeoCPM_120131/GeoCPM_DVWK_T=100a Nullvariante/GEOCPMS.D");
//            final File geocpmIDFile = new File(
//                    "/home/bfriedrich/Desktop/geocpm/2012-02-27/DYNA-GeoCPM_120131/GeoCPM_DVWK_T=100a Nullvariante/GEOCPMI.D");
//
//            final byte[] geocpmFDBytes = new byte[(int)geocpmFDFile.length()];
//            final byte[] geocpmSDBytes = new byte[(int)geocpmSDFile.length()];
//            final byte[] geocpmIDBytes = new byte[(int)geocpmIDFile.length()];
//
//            final FileInputStream geocpmFDIn = new FileInputStream(geocpmFDFile);
//            final FileInputStream geocpmSDIn = new FileInputStream(geocpmSDFile);
//            final FileInputStream geocpmIDIn = new FileInputStream(geocpmIDFile);
//
//            geocpmFDIn.read(geocpmFDBytes);
//            geocpmSDIn.read(geocpmSDBytes);
//            geocpmIDIn.read(geocpmIDBytes);
//
//            geocpmFDIn.close();
//            geocpmSDIn.close();
//            geocpmIDIn.close();
//
//            config.setGeocpmFData(geocpmFDBytes);
//            config.setGeocpmSData(geocpmSDBytes);
//            config.setGeocpmIData(geocpmIDBytes);
//
//            // ---
//
//            final String geocpmData = GeoCPMUtils.readContent(new File(
//                        "/home/bfriedrich/Desktop/geocpm/2012-02-27/DYNA-GeoCPM_120131/GeoCPM_Nullvariante_T=100a/GeoCPM.ein"));
//
//            final String dynaData = GeoCPMUtils.readContent(new File(
//                        "/home/bfriedrich/Desktop/geocpm/2012-02-27/DYNA-GeoCPM_120131/GeoCPM_DVWK_T=100a Nullvariante/DYNA.EIN"));
//
//            config.setGeocpmData(geocpmData);
//            config.setDynaData(dynaData);
//
//            // ---
//
//            config.setGeocpmFolder("GeoCPM_Nullvariante_T=100a");
//            config.setDynaFolder("GeoCPM_DVWK_T=100a Nullvariante");
//
////            final GeoCPMRestServiceImpl restService = new GeoCPMRestServiceImpl();
////            final ImportStatus status = restService.importConfiguration(config);
//
//            final GeoCPMService client = new GeoCPMRestClient("http://localhost:9988/GeoCPM");
//            final ImportStatus status = client.importConfiguration(config);
//
//            System.out.println("Import Status: GeoCPMId: " + status.getGeocpmId());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
