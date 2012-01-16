/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest.io;

import java.io.Serializable;

import java.net.URL;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public final class SimulationResult implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private String geocpmInfo;
    private URL wmsResults;
    private String taskId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SimulationResult object.
     */
    public SimulationResult() {
    }

    /**
     * Creates a new SimulationResult object.
     *
     * @param  geocpmInfo  DOCUMENT ME!
     * @param  wmsResults  DOCUMENT ME!
     * @param  taskId      DOCUMENT ME!
     */
    public SimulationResult(final String geocpmInfo, final URL wmsResults, final String taskId) {
        this.geocpmInfo = geocpmInfo;
        this.wmsResults = wmsResults;
        this.taskId = taskId;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getGeocpmInfo() {
        return geocpmInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geocpmInfo  DOCUMENT ME!
     */
    public void setGeocpmInfo(final String geocpmInfo) {
        this.geocpmInfo = geocpmInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  taskId  DOCUMENT ME!
     */
    public void setTaskId(final String taskId) {
        this.taskId = taskId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public URL getWmsResults() {
        return wmsResults;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  wmsResults  DOCUMENT ME!
     */
    public void setWmsResults(final URL wmsResults) {
        this.wmsResults = wmsResults;
    }
}
