/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest.io;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public final class GeoCPMOutput {

    //~ Instance fields --------------------------------------------------------

    public GeoCPMInfo geoCPMInfo;
    public GeoCPMMax geoCPMMax;
    public GeoCPMSubInfo geoCPMSubInfo;
    public List<ResultsElement> resultsElements = new ArrayList<ResultsElement>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoCPMOutput object.
     */
    public GeoCPMOutput() {
    }
}
