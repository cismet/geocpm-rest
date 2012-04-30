/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest.io;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public final class ImportConfig implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private String geocpmData; // GZIP representation of GEOCPM.EIN content
    private String dynaData;   // GZIP representation of DYNA.EIN content
    private byte[] geocpmFData;
    private byte[] geocpmIData;
    private byte[] geocpmSData;
    private byte[] geocpmNData;

    private String geocpmFolder;
    private String dynaFolder;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ImportConfig object.
     */
    public ImportConfig() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public byte[] getGeocpmNData() {
        return geocpmNData;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geocpmNData  DOCUMENT ME!
     */
    public void setGeocpmNData(final byte[] geocpmNData) {
        this.geocpmNData = geocpmNData;
    }

    /**
     * /** * Creates a new ImportConfig object. * * @param geocpmData DOCUMENT ME! * @param dynaData DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public byte[] getGeocpmFData() {
        return geocpmFData;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geocpmFDData  DOCUMENT ME!
     */
    public void setGeocpmFData(final byte[] geocpmFDData) {
        this.geocpmFData = geocpmFDData;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public byte[] getGeocpmIData() {
        return geocpmIData;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geocpmIData  DOCUMENT ME!
     */
    public void setGeocpmIData(final byte[] geocpmIData) {
        this.geocpmIData = geocpmIData;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public byte[] getGeocpmSData() {
        return geocpmSData;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geocpmSData  DOCUMENT ME!
     */
    public void setGeocpmSData(final byte[] geocpmSData) {
        this.geocpmSData = geocpmSData;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDynaFolder() {
        return dynaFolder;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dynaFolder  DOCUMENT ME!
     */
    public void setDynaFolder(final String dynaFolder) {
        this.dynaFolder = dynaFolder;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getGeocpmFolder() {
        return geocpmFolder;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geocpmFolder  DOCUMENT ME!
     */
    public void setGeocpmFolder(final String geocpmFolder) {
        this.geocpmFolder = geocpmFolder;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDynaData() {
        return dynaData;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dynaData  DOCUMENT ME!
     */
    public void setDynaData(final String dynaData) {
        this.dynaData = dynaData;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getGeocpmData() {
        return geocpmData;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geocpmData  DOCUMENT ME!
     */
    public void setGeocpmData(final String geocpmData) {
        this.geocpmData = geocpmData;
    }
}
