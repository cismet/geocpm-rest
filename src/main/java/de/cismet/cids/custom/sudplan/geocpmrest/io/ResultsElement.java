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
public final class ResultsElement {

    //~ Instance fields --------------------------------------------------------

    public int number;
    public String content;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ResultsElement object.
     */
    public ResultsElement() {
    }
}
