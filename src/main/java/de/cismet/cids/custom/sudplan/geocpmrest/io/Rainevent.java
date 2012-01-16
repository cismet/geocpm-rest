/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest.io;

import java.io.Serializable;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public final class Rainevent implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private Map<Integer, Double> secondsToMm;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Rainevent object.
     */
    public Rainevent() {
    }

    /**
     * Creates a new Rainevent object.
     *
     * @param  secondsToMm  DOCUMENT ME!
     */
    public Rainevent(final Map<Integer, Double> secondsToMm) {
        this.secondsToMm = secondsToMm;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<Integer, Double> getSecondsToMm() {
        return secondsToMm;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  secondsToMm  DOCUMENT ME!
     */
    public void setSecondsToMm(final Map<Integer, Double> secondsToMm) {
        this.secondsToMm = secondsToMm;
    }
}
