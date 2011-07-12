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
public final class Status {

    //~ Static fields/initializers ---------------------------------------------

    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_FINISHED = 2;
    public static final int STATUS_BROKEN = 3;

    //~ Instance fields --------------------------------------------------------

    public boolean indeterminate = true;
    public int percentFinished = -1;
    public int status;
    public String statusDescription;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Status object.
     */
    public Status() {
    }
}
