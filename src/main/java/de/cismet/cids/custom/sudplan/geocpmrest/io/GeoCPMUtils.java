/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest.io;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    public static final String INFO_FILE_NAME = "GeoCPMInfo.aus";       // NOI18N
    public static final String MAX_FILE_NAME = "GeoCPMMax.aus";         // NOI18N
    public static final String SUBINFO_FILE_NAME = "GeoCPMSubInfo.aus"; // NOI18N
    public static final String RES_ELEMENT_NAME = "ResultsElement";     // NOI18N
    public static final String RES_ELEMENT_EXT = ".aus";                // NOI18N
    public static final String GEOCPM_EXE = "GeoCPM.exe";               // NOI18N
    public static final String PID_FILE = "GeoCPM.pid";                 // NOI18N
    public static final String EXEC_STATUS_FINISHED = "Finished";       // NOI18N
    public static final String EXEC_STATUS_BROKEN = "Broken";           // NOI18N
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
     * Reads the output of a GeoCPM run from the given working directory.
     *
     * @param   workingDir  the working directory of the run
     *
     * @return  a {@link GeoCPMOutput} output with the run output
     *
     * @throws  IllegalArgumentException  if the given working directory is is <code>null</code>
     * @throws  IllegalStateException     if there are not exactly three <code>GeoCPM*.aus</code> files or any of these
     *                                    <code>.aus</code> files is unrecognised or if the content of the files cannot
     *                                    be read for any reason
     */
    public static GeoCPMOutput readOutput(final File workingDir) {
        if (workingDir == null) {
            throw new IllegalArgumentException("working dir must not be null"); // NOI18N
        }

        final File[] geocpmOutFiles = workingDir.listFiles(new GeoCPMOutFilter());

        if (geocpmOutFiles.length != 3) {
            throw new IllegalStateException("unexpected number of output files: " + geocpmOutFiles.length); // NOI18N
        }

        final GeoCPMOutput output = new GeoCPMOutput();

        try {
            for (final File geocpmOutFile : geocpmOutFiles) {
                if (INFO_FILE_NAME.equals(geocpmOutFile.getName())) {
                    final GeoCPMInfo info = new GeoCPMInfo();
                    info.content = readContent(geocpmOutFile);
                    output.geoCPMInfo = info;
                } else if (MAX_FILE_NAME.equals(geocpmOutFile.getName())) {
                    final GeoCPMMax max = new GeoCPMMax();
                    max.content = readContent(geocpmOutFile);
                    output.geoCPMMax = max;
                } else if (SUBINFO_FILE_NAME.equals(geocpmOutFile.getName())) {
                    final GeoCPMSubInfo subinfo = new GeoCPMSubInfo();
                    subinfo.content = readContent(geocpmOutFile);
                    output.geoCPMSubInfo = subinfo;
                } else {
                    throw new IllegalStateException("unrecognised output file: " + geocpmOutFile); // NOI18N
                }
            }

            final File[] resultElements = workingDir.listFiles(new ResultElementFilter());

            for (final File resultElementFile : resultElements) {
                final ResultsElement resultElement = new ResultsElement();
                final String number = resultElementFile.getName().replace(RES_ELEMENT_NAME, "") // NOI18N
                    .replace(RES_ELEMENT_EXT, "");                                              // NOI18N

                resultElement.number = Integer.valueOf(number);
                resultElement.content = readContent(resultElementFile);

                output.resultsElements.add(resultElement);
            }
        } catch (final IOException e) {
            final String message = "cannot read output in workingdir: " + workingDir; // NOI18N
            LOG.error(message, e);
            throw new IllegalStateException(message, e);
        }

        return output;
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

                // FIXME: this is a limit for the demo system
                if (sb.length() > 16666) {
                    break;
                }
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
                                final int c = err.read();
                                if (c >= 0) {
                                    System.out.print((char)c);
                                } else {
                                    running = false;
                                }
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
                                final int c = out.read();
                                if (c >= 0) {
                                    System.out.print((char)c);
                                } else {
                                    running = false;
                                }
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
    public static Status getExecutionStatus(final File workingDir, final int pid) {
        final String command = "tasklist /fi \"PID eq " + pid + "\" /v"; // NOI18N

        try {
            final Process p = Runtime.getRuntime().exec(command);
            final BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            int linecount = 1;
            String line = br.readLine();
            final Status status = new Status();
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
                        status.statusDescription = split[6];
                        status.status = Status.STATUS_RUNNING;
                    }
                }
                line = br.readLine();
                ++linecount;
            }

            // we did not find the running process, we check for the info file which is present when the run is finished
            if (status.statusDescription == null) {
                final File[] infoFile = workingDir.listFiles(new RunFinishedFilter());

                assert infoFile.length < 2 : "the run finished filter does not accept more than one file"; // NOI18N

                if (infoFile.length == 0) {
                    status.statusDescription = EXEC_STATUS_BROKEN;
                    status.status = Status.STATUS_BROKEN;
                } else {
                    status.statusDescription = EXEC_STATUS_FINISHED;
                    status.status = Status.STATUS_FINISHED;
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
