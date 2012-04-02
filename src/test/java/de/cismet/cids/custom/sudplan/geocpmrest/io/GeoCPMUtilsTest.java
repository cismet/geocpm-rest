/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest.io;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import de.cismet.tools.FileUtils;

import static org.junit.Assert.*;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class GeoCPMUtilsTest {

    //~ Instance fields --------------------------------------------------------

    private transient File workingDir;
    private transient File resultsDir;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoCPMUtilsTest object.
     */
    public GeoCPMUtilsTest() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    @Before
    public void setUp() {
        final File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        workingDir = new File(tmpDir, "geocpm_utils_test_" + System.currentTimeMillis());

        if (workingDir.exists()) {
            if (!workingDir.isDirectory()) {
                throw new IllegalStateException("cannot run tests, working directory exists and is not a directory: "
                            + workingDir);
            } else if (!workingDir.canRead()) {
                throw new IllegalStateException("cannot run tests, no read permission for working directory: "
                            + workingDir);
            } else if (!workingDir.canWrite()) {
                throw new IllegalStateException("cannot run tests, no write permission for working directory: "
                            + workingDir);
            }
        } else {
            if (!workingDir.mkdirs()) {
                throw new IllegalStateException("cannot run tests, working directory cannot be created: " + workingDir);
            }
        }
        
       resultsDir = new File(workingDir, "0001");

        if (resultsDir.exists()) {
            if (!resultsDir.isDirectory()) {
                throw new IllegalStateException("cannot run tests, results directory exists and is not a directory: "
                            + resultsDir);
            } else if (!resultsDir.canRead()) {
                throw new IllegalStateException("cannot run tests, no read permission for results directory: "
                            + resultsDir);
            } else if (!resultsDir.canWrite()) {
                throw new IllegalStateException("cannot run tests, no write permission for results directory: "
                            + resultsDir);
            }
        } else {
            if (!resultsDir.mkdirs()) {
                throw new IllegalStateException("cannot run tests, results directory cannot be created: " + resultsDir);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDir(workingDir);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getCurrentMethodName() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWriteInput_NullArg() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.writeInput(null, null);
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalStateException.class)
    public void testWriteInput_NullRainevent() {
        System.out.println("TEST " + getCurrentMethodName());

        final GeoCPMInput input = new GeoCPMInput();
        input.rainevent = null;

        GeoCPMUtils.writeInput(input, null);
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalStateException.class)
    public void testWriteInput_NullConfig() {
        System.out.println("TEST " + getCurrentMethodName());

        final GeoCPMInput input = new GeoCPMInput();
        input.rainevent = "";

        GeoCPMUtils.writeInput(input, null);
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalStateException.class)
    public void testWriteInput_NullConfigNotExisting() {
        System.out.println("TEST " + getCurrentMethodName());

        final GeoCPMInput input = new GeoCPMInput();
        input.rainevent = "";

        GeoCPMUtils.writeInput(input, new File(String.valueOf(System.currentTimeMillis())));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test(expected = IllegalStateException.class)
    public void testWriteInput_NullConfigNotReadable() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final GeoCPMInput input = new GeoCPMInput();
        input.rainevent = "";

        final File config = new File(workingDir, String.valueOf(System.currentTimeMillis()));
        config.createNewFile();
        config.setReadable(false);

        GeoCPMUtils.writeInput(input, config);
    }

    /**
     * combined test to ensure read and write are bijective.
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testWriteInputReadContent() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final GeoCPMInput input = new GeoCPMInput();
        input.rainevent = "TestString\nsecondline\nThirdline$$%";

        final File config = new File(workingDir, String.valueOf(System.currentTimeMillis()));
        config.createNewFile();
        final BufferedWriter bw = new BufferedWriter(new FileWriter(config));
        bw.write("ABCDEFG");
        bw.newLine();
        bw.write("HIJKLM  ");
        bw.newLine();
        bw.newLine();
        bw.flush();
        bw.close();

        File f = GeoCPMUtils.writeInput(input, config);

        BufferedReader r = new BufferedReader(new FileReader(f));
        String line;
        int lineCount = 0;
        while ((line = r.readLine()) != null) {
            ++lineCount;
            switch (lineCount) {
                case 1: {
                    assertEquals("ABCDEFG", line);
                    break;
                }
                case 2: {
                    assertEquals("HIJKLM  ", line);
                    break;
                }
                case 3: {
                    assertEquals("", line);
                    break;
                }
                case 4: {
                    assertEquals("", line);
                    break;
                }
                case 5: {
                    assertEquals("TestString", line);
                    break;
                }
                case 6: {
                    assertEquals("secondline", line);
                    break;
                }
                case 7: {
                    assertEquals("Thirdline$$%", line);
                    break;
                }
            }
        }
        r.close();

        assertEquals("illegal linecount", 7, lineCount);

        String content = GeoCPMUtils.readContent(f);

        assertEquals("content not equal", "ABCDEFG\nHIJKLM  \n\n\n" + input.rainevent, content);

        FileUtils.deleteDir(f.getParentFile());

        input.rainevent = "TestString\nsecondline\nThirdline$$%\n\n\n";

        f = GeoCPMUtils.writeInput(input, config);

        r = new BufferedReader(new FileReader(f));
        lineCount = 0;
        while ((line = r.readLine()) != null) {
            ++lineCount;
            switch (lineCount) {
                case 1: {
                    assertEquals("ABCDEFG", line);
                    break;
                }
                case 2: {
                    assertEquals("HIJKLM  ", line);
                    break;
                }
                case 3: {
                    assertEquals("", line);
                    break;
                }
                case 4: {
                    assertEquals("", line);
                    break;
                }
                case 5: {
                    assertEquals("TestString", line);
                    break;
                }
                case 6: {
                    assertEquals("secondline", line);
                    break;
                }
                case 7: {
                    assertEquals("Thirdline$$%", line);
                    break;
                }
            }
        }
        r.close();

        assertEquals("illegal linecount", 9, lineCount);

        content = GeoCPMUtils.readContent(f);

        assertEquals("content not equal", "ABCDEFG\nHIJKLM  \n\n\n" + input.rainevent, content);

        FileUtils.deleteDir(f.getParentFile());
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReadOutput_nullArg() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.readOutput(null);
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalStateException.class)
    public void testReadOutput_toFewGeoCPMFiles() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.readOutput(workingDir);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test(expected = IllegalStateException.class)
    public void testReadOutput_toManyGeoCPMFiles() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        File f = new File(workingDir, "GeoCPM1.aus");
        assertTrue(f.createNewFile());
        f = new File(workingDir, "GeoCPM2.aus");
        assertTrue(f.createNewFile());
        f = new File(workingDir, "GeoCPM3.aus");
        assertTrue(f.createNewFile());
        f = new File(workingDir, "GeoCPM4.aus");
        assertTrue(f.createNewFile());

        GeoCPMUtils.readOutput(workingDir);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test(expected = IllegalStateException.class)
    public void testReadOutput_unrecognisedGeoCPMFiles() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        File f = new File(workingDir, "GeoCPM1.aus");
        assertTrue(f.createNewFile());
        f = new File(workingDir, "GeoCPM2.aus");
        assertTrue(f.createNewFile());
        f = new File(workingDir, "GeoCPM3.aus");
        assertTrue(f.createNewFile());

        GeoCPMUtils.readOutput(workingDir);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testReadOutput() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        writeFile(GeoCPMUtils.INFO_FILE_NAME, this.resultsDir);
        writeFile(GeoCPMUtils.MAX_FILE_NAME, this.resultsDir);
        writeFile(GeoCPMUtils.SUBINFO_FILE_NAME, this.workingDir);
        writeFile("ResultsElement831.aus", this.resultsDir);
        writeFile("ResultsElement835.aus", this.resultsDir);
        writeFile("ResultsElement836.aus", this.resultsDir);
        writeFile("ResultsElement1126.aus", this.resultsDir);
        writeFile("ResultsElement1128.aus", this.resultsDir);
        writeFile("ResultsElement1129.aus", this.resultsDir);
        writeFile("ResultsElement1130.aus", this.resultsDir);
        writeFile("ResultsElement1131.aus", this.resultsDir);
        writeFile("ResultsElement1132.aus", this.resultsDir);
        writeFile("ResultsElement1139.aus", this.resultsDir);

        final GeoCPMOutput output = GeoCPMUtils.readOutput(workingDir);

        assertNotNull(output);
        assertNotNull(output.geoCPMInfo);
        assertNotNull(output.geoCPMInfo.content);
        assertEquals("illegal info content", 822, output.geoCPMInfo.content.length());
        assertNotNull(output.geoCPMMax);
        assertNotNull(output.geoCPMMax.content);
        assertEquals("illegal max content", 10978, output.geoCPMMax.content.length());
        assertNotNull(output.geoCPMSubInfo);
        assertNotNull(output.geoCPMSubInfo.content);
        assertEquals("illegal subinfo content", 14, output.geoCPMSubInfo.content.length());
        assertNotNull(output.resultsElements);
        assertEquals("not all resultelements read", 10, output.resultsElements.size());

        for (final ResultsElement e : output.resultsElements) {
            assertNotNull(e.content);

            switch (e.number) {
                case 831: {
                    assertEquals("illegal resultinfo content 831", 77, e.content.length());
                    break;
                }
                case 835: {
                    assertEquals("illegal resultinfo content 835", 77, e.content.length());
                    break;
                }
                case 836: {
                    assertEquals("illegal resultinfo content 836", 96, e.content.length());
                    break;
                }
                case 1126: {
                    assertEquals("illegal resultinfo content 1126", 75, e.content.length());
                    break;
                }
                case 1128: {
                    assertEquals("illegal resultinfo content 1128", 75, e.content.length());
                    break;
                }
                case 1129: {
                    assertEquals("illegal resultinfo content 1129", 77, e.content.length());
                    break;
                }
                case 1130: {
                    assertEquals("illegal resultinfo content 1130", 77, e.content.length());
                    break;
                }
                case 1131: {
                    assertEquals("illegal resultinfo content 1131", 77, e.content.length());
                    break;
                }
                case 1132: {
                    assertEquals("illegal resultinfo content 1132", 75, e.content.length());
                    break;
                }
                case 1139: {
                    assertEquals("illegal resultinfo content 1139", 75, e.content.length());
                    break;
                }
                default: {
                    fail("unrecognised result element number: " + e.number);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   resource  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void writeFile(final String resource, final File targetDir) throws IOException {
        final BufferedInputStream bis = new BufferedInputStream(getClass().getResourceAsStream(resource));
        final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(targetDir, resource)));
        final byte[] buff = new byte[1024];
        int read = bis.read(buff);
        while (read != -1) {
            bos.write(buff, 0, read);
            read = bis.read(buff);
        }

        bis.close();
        bos.close();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReadContent_NullArg() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.readContent(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test(expected = IOException.class)
    public void testReadContent_IllegalFile() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.readContent(new File("abc"));
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReadPid_nullArg() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.readPid(null);
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalStateException.class)
    public void testReadPid_IllegalFile() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.readPid(new File("abc"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test(expected = IllegalStateException.class)
    public void testReadPid_IllegalFileContent() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final File f = new File(workingDir, GeoCPMUtils.PID_FILE);
        f.createNewFile();
        final BufferedWriter w = new BufferedWriter(new FileWriter(f));
        w.write("abc");
        w.close();

        GeoCPMUtils.readPid(workingDir);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testReadPid() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final File f = new File(workingDir, GeoCPMUtils.PID_FILE);
        f.createNewFile();
        final BufferedWriter w = new BufferedWriter(new FileWriter(f));
        w.write("1111");
        w.close();

        final int pid = GeoCPMUtils.readPid(workingDir);

        assertEquals("illegal pid", 1111, pid);
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateId_NullFileArg() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.createId(null, 0);
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateId_IllegalFileArg() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.createId(new File("abc"), 0);
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateId_0PidArg() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.createId(workingDir, 0);
    }

    /**
     * DOCUMENT ME!
     */
    @Test
    public void testCreateId() {
        System.out.println("TEST " + getCurrentMethodName());

        final String id = GeoCPMUtils.createId(new File(workingDir, "inputfile"), 1234);

        assertEquals("illegal id", workingDir.getName() + "@" + 1234, id);
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetWorkingDir_nullArg() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.getWorkingDir(null);
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetWorkingDir_noAt() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.getWorkingDir("abc");
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetWorkingDir_doubleAt() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.getWorkingDir("abc@b@a");
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalStateException.class)
    public void testGetWorkingDir_IllegalFile() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.getWorkingDir("abc@0");
    }

    /**
     * DOCUMENT ME!
     */
    @Test
    public void testGetWorkingDir() {
        System.out.println("TEST " + getCurrentMethodName());

        final String runId = workingDir.getName() + "@" + 0;

        final File wd = GeoCPMUtils.getWorkingDir(runId);

        assertEquals(workingDir, wd);
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPid_nullArg() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.getPid(null);
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPid_noAt() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.getPid("abc");
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPid_doubleAt() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.getPid("abc@b@a");
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPid_noNumber() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.getPid("abc@a");
    }

    /**
     * DOCUMENT ME!
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetPid_illegalPid() {
        System.out.println("TEST " + getCurrentMethodName());

        GeoCPMUtils.getPid("abc@0");
    }

    /**
     * DOCUMENT ME!
     */
    @Test
    public void testGetPid() {
        System.out.println("TEST " + getCurrentMethodName());

        final int pid = GeoCPMUtils.getPid("abc@1");

        assertEquals("illegal pid", 1, pid);
    }

    /**
     * DOCUMENT ME!
     */
    @Ignore
    @Test
    public void testGetExecutionStatus() {
        // can only be tested on windows machine
    }
}
