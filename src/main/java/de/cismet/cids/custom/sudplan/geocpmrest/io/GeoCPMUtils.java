/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest.io;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import java.util.Properties;
import java.util.zip.GZIPOutputStream;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class GeoCPMUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(GeoCPMUtils.class);

    public static final String INPUT_FILE_NAME = "GeoCPM.ein";          // NOI18N
    public static final String RESULTS_FOLDER = "0001";                 // NOI18N
    public static final String INFO_FILE_NAME = "GeoCPMInfo.aus";       // NOI18N
    public static final String MAX_FILE_NAME = "GeoCPMMax.aus";         // NOI18N
    public static final String SUBINFO_FILE_NAME = "GeoCPMSubInfo.aus"; // NOI18N
    public static final String RES_ELEMENT_NAME = "ResultsElement";     // NOI18N
    public static final String RES_ELEMENT_EXT = ".aus";                // NOI18N
    public static final String GEOCPM_EXE = "dyna.exe";                 // NOI18N
    public static final String PID_FILE = "dyna.pid";                   // NOI18N
    public static final String TOKEN_RAINCURVE = "RAINCURVE";           // NOI18N

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoCPMUtils object.
     */
    private GeoCPMUtils() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Writes a given {@link GeoCPMInput} to a newly created working directory and returns a handle to the input file.
     *
     * @param   input   the <code>GeoCPMInput</code> for a run
     * @param   config  DOCUMENT ME!
     *
     * @return  a handle to the written <code>GeoCPMInput</code>
     *
     * @throws  IllegalArgumentException  if the given input is <code>null</code>
     * @throws  IllegalStateException     if the given input's content is <code>null</code> or the file cannot be
     *                                    written for any reason
     */
    public static File writeInput(final GeoCPMInput input, final File config) {
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");            // NOI18N
        }
        if (input.rainevent == null) {
            throw new IllegalStateException("timeseries of input must not be null"); // NOI18N
        }
        if (config == null) {
            throw new IllegalStateException("config must not be null");              // NOI18N
        } else if (!config.exists()) {
            throw new IllegalStateException("config must exist");                    // NOI18N
        } else if (!config.canRead()) {
            throw new IllegalStateException("config must be readable");              // NOI18N
        }

        BufferedWriter bw = null;
        BufferedReader br = null;
        try {
            final File tmpDir = new File(System.getProperty("java.io.tmpdir"));           // NOI18N
            final File outDir = new File(tmpDir, "geocpm_" + System.currentTimeMillis()); // NOI18N
            if (!outDir.mkdir()) {
                throw new IOException("cannot create tmp dir");                           // NOI18N
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("writing input to dir: " + outDir.getAbsolutePath()); // NOI18N
            }

            final File outFile = new File(outDir, INPUT_FILE_NAME);

            bw = new BufferedWriter(new FileWriter(outFile));
            br = new BufferedReader(new FileReader(config));

            String line = br.readLine();

            while (line != null) {
                if (line.startsWith(TOKEN_RAINCURVE)) {
                    // we assume that the raincurve is the very last block in the input file
                    break;
                } else {
                    bw.write(line);
                    bw.newLine();
                }

                line = br.readLine();
            }

            bw.newLine();
            bw.write(input.rainevent);

            return outFile;
        } catch (final IOException e) {
            final String message = "error while writing input file: " + input; // NOI18N
            LOG.error(message, e);
            throw new IllegalStateException(message, e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (final IOException e) {
                    LOG.warn("cannot close writer", e);                        // NOI18N
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    LOG.warn("cannot close reader", e);                        // NOI18N
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   file  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private static void checkFile(final File file) {
        if (!file.exists()) {
            throw new IllegalStateException("File " + file.getName() + " does not exist");
        }

        if (!file.canRead()) {
            throw new IllegalStateException("File " + file.getName() + " can not be read");
        }
    }

    /**
     * Reads the output of a GeoCPM run from the given working directory.
     *
     * @param   geocpmEinDir  workingDir the directory where the GeoCPM.EIN file is located
     *
     * @return  a {@link GeoCPMOutput} output with the run output
     *
     * @throws  IllegalArgumentException  if the given working directory is is <code>null</code>
     * @throws  IllegalStateException     if there are not exactly three <code>GeoCPM*.aus</code> files or any of these
     *                                    <code>.aus</code> files is unrecognised or if the content of the files cannot
     *                                    be read for any reason
     */
    public static GeoCPMOutput readOutput(final File geocpmEinDir) {
        if (geocpmEinDir == null) {
            throw new IllegalArgumentException("geocpm output folder must not be null"); // NOI18N
        }

        final GeoCPMOutput output = new GeoCPMOutput();

        try {
            final File geocpmSubInfo = new File(geocpmEinDir, SUBINFO_FILE_NAME);
            checkFile(geocpmSubInfo);
            final GeoCPMSubInfo subinfo = new GeoCPMSubInfo();
            subinfo.content = readContent(geocpmSubInfo);
            output.geoCPMSubInfo = subinfo;

            final File resultsFolder = new File(geocpmEinDir, RESULTS_FOLDER);

            final File geocpmInfo = new File(resultsFolder, INFO_FILE_NAME);
            checkFile(geocpmInfo);
            final GeoCPMInfo info = new GeoCPMInfo();
            info.content = readContent(geocpmInfo);
            output.geoCPMInfo = info;

            final File geocpmMax = new File(resultsFolder, MAX_FILE_NAME);
            checkFile(geocpmMax);
            final GeoCPMMax max = new GeoCPMMax();
            max.content = readContent(geocpmMax);
            output.geoCPMMax = max;

            final File[] resultElements = resultsFolder.listFiles(new ResultElementFilter());

            for (final File resultElementFile : resultElements) {
                final ResultsElement resultElement = new ResultsElement();
                final String number = resultElementFile.getName().replace(RES_ELEMENT_NAME, "") // NOI18N
                    .replace(RES_ELEMENT_EXT, "");                                              // NOI18N

                resultElement.number = Integer.valueOf(number);
                resultElement.content = readContent(resultElementFile);

                output.resultsElements.add(resultElement);
            }
        } catch (final IOException e) {
            final String message = "cannot read output in geocpm output dir: " + geocpmEinDir; // NOI18N
            LOG.error(message, e);
            throw new IllegalStateException(message, e);
        }

        return output;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   workingDir  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException            DOCUMENT ME!
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public static String readInfo(final File workingDir) throws IOException {
        final File[] infoFileArray = workingDir.listFiles(new FileFilter() {

                    @Override
                    public boolean accept(final File pathname) {
                        return pathname.getPath().endsWith(INFO_FILE_NAME);
                    }
                });

        if (infoFileArray.length == 1) {
            return readContent(infoFileArray[0]);
        } else {
            throw new IllegalStateException("there is not exactly one info file: " + workingDir); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   toRead  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static String readContentGzip(final File toRead) throws IOException {
        if (toRead == null) {
            throw new IllegalArgumentException("file to read must not be null"); // NOI18N
        }

        final PipedOutputStream pos = new PipedOutputStream();
        final PipedInputStream pis = new PipedInputStream(pos);

        final PipingFileReader pr = new PipingFileReader(toRead, pos);
        final PipedGZipStringWriter pw = new PipedGZipStringWriter(pis, "Windows-1256"); // NOI18N

        final Thread reader = new Thread(pr, "geocpm info pipe reader"); // NOI18N
        final Thread writer = new Thread(pw, "geocpm info pipe writer"); // NOI18N

        reader.start();
        writer.start();
        try {
            reader.join();
            writer.join();
        } catch (final InterruptedException ex) {
            final String message = "cannot wait threads to finish work"; // NOI18N
            LOG.error(message, ex);
            throw new IOException(message, ex);
        }

        if (pr.getException() != null) {
            final String message = "error while reading from file";      // NOI18N
            LOG.error(message, pr.getException());
            throw new IOException(message, pr.getException());
        }
        if (pw.getException() != null) {
            final String message = "error while writing to gzip string"; // NOI18N
            LOG.error(message, pw.getException());
            throw new IOException(message, pw.getException());
        }

        return pw.getResult();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   file  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  NullPointerException      DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static byte[] readBytes(final File file) throws IOException {
        if (file == null) {
            throw new NullPointerException();
        }

        if (!file.exists()) {
            throw new IllegalArgumentException("File " + file + " does not exist");
        }

        if (file.isDirectory()) {
            throw new IllegalArgumentException("File " + file + " is a directory");
        }

        final byte[] buffer = new byte[(int)file.length()];

        FileInputStream fin = null;

        try {
            fin = new FileInputStream(file);
            fin.read(buffer);
        } catch (final IOException e) {
            final String message = "error while reading bytes from file " + file;
            LOG.error(message, e);
            throw new IOException(message, e);
        } finally {
            if (fin != null) {
                fin.close();
            }
        }

        return buffer;
    }

    /**
     * Reads the content of a given file using Windows-1256 encoding.
     *
     * @param   toRead  file whose content shall be read
     *
     * @return  the content of the file as {@link String}
     *
     * @throws  IOException               if the given file cannot be read for any reason
     * @throws  IllegalArgumentException  if the given file is <code>null</code>
     */
    public static String readContent(final File toRead) throws IOException {
        if (toRead == null) {
            throw new IllegalArgumentException("file to read must not be null"); // NOI18N
        }

        final StringBuilder sb = new StringBuilder();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(toRead), "Windows-1256")); // NOI18N

            int c = br.read();
            while (c != -1) {
                sb.append((char)c);

                c = br.read();

//                // FIXME: this is a limit for the demo system
//                if (sb.length() > 16666) {
//                    break;
//                }
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    LOG.warn("cannot close reader", e); // NOI18N
                }
            }
        }

        return sb.toString();
    }

    /**
     * Reads the process id from a given working directory. Expects a <code>GeoCPM.pid</code> file to be in the working
     * directory
     *
     * @param   workingDir  the working directory of the run
     *
     * @return  the process id of the run
     *
     * @throws  IllegalArgumentException  if the given working directory is <code>null</code>
     * @throws  IllegalStateException     if any error occurs reading the pid file
     */
    public static int readPid(final File workingDir) {
        if (workingDir == null) {
            throw new IllegalArgumentException("working dir must not be null"); // NOI18N
        }

        final File pidFile = new File(workingDir, PID_FILE);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(pidFile));

            return Integer.parseInt(br.readLine());
        } catch (final Exception e) {
            final String message = "cannot read pid file in working dir: " + workingDir; // NOI18N
            LOG.error(message, e);
            throw new IllegalStateException(message, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    LOG.warn("cannot close reader", e);                                  // NOI18N
                }
            }
        }
    }

    /**
     * Creates a run id from the run input file and the process pid. Format: <code>&lt;workingDir&gt;@&lt;pid&gt;</code>
     *
     * @param   inputFile  the input file of the run
     * @param   pid        the process id of the run
     *
     * @return  the run id
     *
     * @throws  IllegalArgumentException  if the inputfile is <code>null</code> or the inputfile has not parent or the
     *                                    pid is less than 1
     */
    public static String createId(final File inputFile, final int pid) {
        if (inputFile == null) {
            throw new IllegalArgumentException("input file must not be null");                     // NOI18N
        } else if (inputFile.getParent() == null) {
            throw new IllegalArgumentException("input file does not have a parent: " + inputFile); // NOI18N
        } else if (pid < 1) {
            throw new IllegalArgumentException("illegal pid: " + pid);
        }

        return inputFile.getParentFile().getName() + "@" + pid; // NOI18N
    }

    /**
     * Finds the working directory for a given run id.
     *
     * @param   id  the run id
     *
     * @return  the working directory of the run
     *
     * @throws  IllegalArgumentException  if the given id is <code>null</code> or the id is not of the format as of
     *                                    {@link #createId(java.io.File, int)}
     * @throws  IllegalStateException     if the working dir for the given id does not exist or is not a directory
     */
    public static File getWorkingDir(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null"); // NOI18N
        }

        final String[] split = id.split("@"); // NOI18N

        if (split.length != 2) {
            throw new IllegalArgumentException("illegal id: " + id); // NOI18N
        }

        final File tmpDir = new File(System.getProperty("java.io.tmpdir")); // NOI18N
        final File workDir = new File(tmpDir, split[0]);

        if (!workDir.exists() || !workDir.isDirectory()) {
            throw new IllegalStateException("cannot find working dir: " + id); // NOI18N
        }

        return workDir;
    }

    /**
     * Reads-in export meta data from the specified directory.
     *
     * @param   dir  directory (which is usually the working directory) the export meta data shall be read from
     *
     * @return  export meta data
     *
     * @throws  GeoCPMException           if an error occurs while reading export meta data from dir
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static Properties getExportMetaData(final File dir) throws GeoCPMException {
        if (dir == null) {
            final String message = "Given directory must not be null";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        if (!dir.isDirectory()) {
            final String message = "Given file has to be a directory";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        final File exportMetaDataFile = new File(dir.getAbsolutePath(), "geocpm_export_meta.properties");

        if (!exportMetaDataFile.canRead()) {
            final String message = "Can not read file " + exportMetaDataFile.getAbsolutePath();
            LOG.error(message);
            throw new GeoCPMException(message);
        }

        final Properties exportMetaData = new Properties();
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(exportMetaDataFile);
            exportMetaData.load(fin);
            return exportMetaData;
        } catch (final Exception ex) {
            final String message = "An error occurred while loading export meta data";
            LOG.error(message, ex);
            throw new GeoCPMException(message, ex);
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new GeoCPMException(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * Finds the process id for a given run id.
     *
     * @param   id  the run id
     *
     * @return  the run's process id
     *
     * @throws  IllegalArgumentException  if the given id is <code>null</code> or the id is not of the format as of
     *                                    {@link #createId(java.io.File, int)} or the id is illegal
     */
    public static int getPid(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null"); // NOI18N
        }

        final String[] split = id.split("@"); // NOI18N

        if (split.length != 2) {
            throw new IllegalArgumentException("illegal id: " + id); // NOI18N
        }

        final int pid;
        try {
            pid = Integer.parseInt(split[1]);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("illegal pid: " + split[1]); // NOI18N
        }

        if (pid < 1) {
            throw new IllegalArgumentException("illegal pid, must be > 1: " + pid); // NOI18N
        }

        return pid;
    }

    /**
     * Drains the stdin and stderr of the given process and prints the content to <code>System.out</code> or <code>
     * System.err</code> respectively. The method returns immediately after the drainer threads are started.
     *
     * @param  process  the process whose stdin and stderr shall be drained
     */
    public static void drainStreams(final Process process) {
        final InputStream err = process.getErrorStream();
        final InputStream out = process.getInputStream();

        final Thread errDrainer = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        boolean running = true;
                        while (running) {
                            try {
                                final BufferedReader reader = new BufferedReader(new InputStreamReader(out));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    LOG.info(line);
                                }
                                running = false;
                            } catch (final Exception e) {
                                LOG.error("error while draining system err for process: " + process, e); // NOI18N
                            }
                        }

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("errdrainer stopped: " + process); // NOI18N
                        }
                    }
                });
        errDrainer.start();

        if (LOG.isDebugEnabled()) {
            LOG.debug("errdrainer started: " + process); // NOI18N
        }

        final Thread outDrainer = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        boolean running = true;
                        while (running) {
                            try {
                                final BufferedReader reader = new BufferedReader(new InputStreamReader(err));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    LOG.warn(line);
                                }
                                running = false;
                            } catch (final Exception e) {
                                LOG.error("error while draining system out for process: " + process, e); // NOI18N
                            }
                        }

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("outdrainer stopped: " + process); // NOI18N
                        }
                    }
                });
        outDrainer.start();

        if (LOG.isDebugEnabled()) {
            LOG.debug("outdrainer started: " + process); // NOI18N
        }
    }

    /**
     * This is a highly platform dependant implementation to test if a certain process is still running or not.<br/>
     * <br/>
     * <b>Tested on windows vista ultimate, may differ for other windows versions. Depends on the <code>tasklist</code>
     * command</b>
     *
     * @param   workingDir  the working directory of the run to test
     * @param   pid         the process id of the run to test
     *
     * @return  a {@link Status} object describing the current execution status
     *
     * @throws  IllegalStateException  if the format of the <code>tasklist</code> command differs from the one that was
     *                                 basis for this implementation or if any other error occurs during status
     *                                 determination.
     */
    public static ExecutionStatus getExecutionStatus(final File workingDir, final int pid) {
        final String command = "tasklist /fi \"PID eq " + pid + "\" /v"; // NOI18N

        try {
            final Process p = Runtime.getRuntime().exec(command);
            final BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            int linecount = 1;
            String line = br.readLine();
            final ExecutionStatus status = new ExecutionStatus();
            while (line != null) {
                // first line is empty, second are the column headers, third are separators
                if (linecount < 4) {
                    // do nothing
                } else if (linecount > 4) {
                    throw new IllegalStateException("did not expect more than three lines"); // NOI18N
                } else {
                    if (line.startsWith(GEOCPM_EXE)) {
                        final String[] split = line.split("\\s+");                           // NOI18N
                        // seventh entry is the current status
                        status.setStatusDesc(split[6]);
                        status.setStatus(ExecutionStatus.RUNNING);
                    }
                }
                line = br.readLine();
                ++linecount;
            }

            // we did not find the running process, we check for the info file which is present when the run is finished
            if (status.getStatusDesc() == null) {
                final File resultsFolder = new File(workingDir, RESULTS_FOLDER);
                final File[] infoFile = resultsFolder.listFiles(new RunFinishedFilter());

                assert infoFile.length < 2 : "the run finished filter does not accept more than one file"; // NOI18N

                if (infoFile.length == 0) {
                    status.setStatusDesc("the run is not running anymore and no results were found, considered broken"); // NOI18N
                    status.setStatus(ExecutionStatus.BROKEN);
                } else {
                    status.setStatusDesc("the run is finished");                                                         // NOI18N
                    status.setStatus(ExecutionStatus.FINISHED);
                }
            }

            return status;
        } catch (final IOException ex) {
            final String message = "cannot fetch status information for pid: " + pid; // NOI18N
            LOG.error(message, ex);
            throw new IllegalStateException(message, ex);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class PipingFileReader implements Runnable {

        //~ Instance fields ----------------------------------------------------

        private final transient File toRead;
        private final transient PipedOutputStream pos;
        private transient Exception e;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PipingFileReader object.
         *
         * @param   toRead  DOCUMENT ME!
         * @param   pos     DOCUMENT ME!
         *
         * @throws  IllegalArgumentException  DOCUMENT ME!
         */
        PipingFileReader(final File toRead, final PipedOutputStream pos) {
            if (toRead == null) {
                throw new IllegalArgumentException("toRead must not be null");
            }
            if (pos == null) {
                throw new IllegalArgumentException("pos must not be null");
            }

            this.toRead = toRead;
            this.pos = pos;
            this.e = null;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(toRead));
                final byte[] buffer = new byte[8192];
                int read;
                while ((read = bis.read(buffer)) != -1) {
                    pos.write(buffer, 0, read);
                }
            } catch (final Exception ex) {
                LOG.error("cannot read geocpm info", ex);
                this.e = ex;
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (final IOException ex) {
                        LOG.warn("cannot close geocpm info inputstream", ex); // NOI18N
                    }
                }

                try {
                    pos.close();
                } catch (final IOException ex) {
                    LOG.warn("could not close pipe", ex); // NOI18N
                }
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Exception getException() {
            return e;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class PipedGZipStringWriter implements Runnable {

        //~ Instance fields ----------------------------------------------------

        private final transient PipedInputStream pis;
        private final transient String encoding;
        private transient String result;
        private transient Exception e;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PipedGZipStringWriter object.
         *
         * @param   pis       DOCUMENT ME!
         * @param   encoding  DOCUMENT ME!
         *
         * @throws  IllegalArgumentException  DOCUMENT ME!
         */
        PipedGZipStringWriter(final PipedInputStream pis, final String encoding) {
            if (encoding == null) {
                throw new IllegalArgumentException("encoding must not be null");
            }
            if (pis == null) {
                throw new IllegalArgumentException("pis must not be null");
            }

            this.encoding = encoding;
            this.pis = pis;
            this.e = null;
            this.result = null;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            GZIPOutputStream gos = null;
            try {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                gos = new GZIPOutputStream(baos);
                final byte[] buffer = new byte[8192];
                int read;
                while ((read = pis.read(buffer)) != -1) {
                    gos.write(buffer, 0, read);
                }

                gos.flush();
                gos.finish();

                result = baos.toString(encoding);
            } catch (final Exception ex) {
                LOG.error("cannot read geocpm info", ex);                     // NOI18N
                this.e = ex;
            } finally {
                if (gos != null) {
                    try {
                        gos.close();
                    } catch (final IOException ex) {
                        LOG.warn("cannot close piped gzip outputstream", ex); // NOI18N
                    }
                }

                try {
                    pis.close();
                } catch (final IOException ex) {
                    LOG.warn("could not close pipe", ex); // NOI18N
                }
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Exception getException() {
            return e;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getResult() {
            return result;
        }
    }

    /**
     * Filter for GeoCPM <code>ResultsElement*.aus</code> files.
     *
     * @version  $Revision$, $Date$
     */
    public static final class ResultElementFilter implements FileFilter {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean accept(final File pathname) {
            final String filename = pathname.getName();

            return filename.startsWith(RES_ELEMENT_NAME) && filename.endsWith(RES_ELEMENT_EXT); // NOI18N
        }
    }

    /**
     * Filter for <code>GeoCPM*.aus</code> files.
     *
     * @version  $Revision$, $Date$
     */
    public static final class GeoCPMOutFilter implements FileFilter {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean accept(final File pathname) {
            final String filename = pathname.getName();

            return filename.startsWith("GeoCPM") && filename.endsWith(".aus"); // NOI18N
        }
    }

    /**
     * Filter for <code>GeoCPMInfo.aus</code> file that indicates a finished run.
     *
     * @version  $Revision$, $Date$
     */
    public static final class RunFinishedFilter implements FileFilter {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean accept(final File pathname) {
            return INFO_FILE_NAME.equals(pathname.getName());
        }
    }
}
