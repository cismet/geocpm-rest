/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.geocpmrest.io;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public final class GeoCPMInput {

    //~ Instance fields --------------------------------------------------------

    public String configName;
    public String rainevent;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeoCPMInput object.
     */
    public GeoCPMInput() {
    }
}
