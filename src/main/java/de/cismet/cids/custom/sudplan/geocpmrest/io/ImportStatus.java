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
public final class ImportStatus implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private Integer geocpmId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ImportStatus object.
     */
    public ImportStatus() {
    }

    /**
     * Creates a new ImportStatus object.
     *
     * @param  geocpmId  DOCUMENT ME!
     */
    public ImportStatus(final Integer geocpmId) {
        this.geocpmId = geocpmId;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getGeocpmId() {
        return geocpmId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geocpmId  DOCUMENT ME!
     */
    public void setGeocpmId(final Integer geocpmId) {
        this.geocpmId = geocpmId;
    }
}
