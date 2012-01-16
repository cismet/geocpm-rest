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

    private String geocpmData;
    private String dynaData;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ImportConfig object.
     */
    public ImportConfig() {
    }

    /**
     * Creates a new ImportConfig object.
     *
     * @param  geocpmData  DOCUMENT ME!
     * @param  dynaData    DOCUMENT ME!
     */
    public ImportConfig(final String geocpmData, final String dynaData) {
        this.geocpmData = geocpmData;
        this.dynaData = dynaData;
    }

    //~ Methods ----------------------------------------------------------------

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
