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
public final class SimulationConfig implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private int geocpmCfg;
    private Rainevent rainevent;
    private boolean combinedRun;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SimulationConfig object.
     */
    public SimulationConfig() {
    }

    /**
     * Creates a new SimulationConfig object.
     *
     * @param  geocpmCfg    DOCUMENT ME!
     * @param  rainevent    DOCUMENT ME!
     * @param  combinedRun  DOCUMENT ME!
     */
    public SimulationConfig(final int geocpmCfg, final Rainevent rainevent, final boolean combinedRun) {
        this.geocpmCfg = geocpmCfg;
        this.rainevent = rainevent;
        this.combinedRun = combinedRun;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isCombinedRun() {
        return combinedRun;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  combinedRun  DOCUMENT ME!
     */
    public void setCombinedRun(final boolean combinedRun) {
        this.combinedRun = combinedRun;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getGeocpmCfg() {
        return geocpmCfg;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geocpmCfg  DOCUMENT ME!
     */
    public void setGeocpmCfg(final int geocpmCfg) {
        this.geocpmCfg = geocpmCfg;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Rainevent getRainevent() {
        return rainevent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rainevent  DOCUMENT ME!
     */
    public void setRainevent(final Rainevent rainevent) {
        this.rainevent = rainevent;
    }
}
